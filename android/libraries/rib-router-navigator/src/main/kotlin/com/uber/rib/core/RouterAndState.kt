/*
 * Copyright (C) 2023. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core

import androidx.annotation.VisibleForTesting
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Internal class for keeping track of a navigation stack.
 *
 * @param state The associated state
 * @param attachTransition The [RouterNavigator.AttachTransition] associated with this state.
 * @param detachTransition The [RouterNavigator.DetachTransition] associated with this state.
 * @param forceRouterCaching Override [RouterNavigatorState.isCacheable] behavior
 */
internal class RouterAndState<R : Router<*>, StateT : RouterNavigatorState>(
  val state: StateT,
  private val attachTransition: RouterNavigator.AttachTransition<R, StateT>,
  detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  @get:VisibleForTesting val forceRouterCaching: Boolean = false,
) {
  private val routerAccessor = SafeRouterAccessor(attachTransition::buildRouter)

  /**
   * Gets or creates the [Router] associated with this state. Router will be destroyed after
   * [RouterNavigator.DetachCallback.onPostDetachFromHost] and if [StateT] is cacheable.
   */
  internal val router: R by routerAccessor::router

  internal fun willAttachToHost(previousState: StateT?, isPush: Boolean) =
    routerAccessor.safeOperation { safeRouter ->
      attachTransition.willAttachToHost(safeRouter, previousState, state, isPush)
    }

  internal fun willDetachFromHost(newState: StateT?, isPush: Boolean) =
    routerAccessor.safeOperation { safeRouter ->
      detachCallback.willDetachFromHost(safeRouter, state, newState, isPush)
    }

  internal fun onPostDetachFromHost(newState: StateT?, isPush: Boolean) =
    routerAccessor.safeOperation { safeRouter ->
      detachCallback.onPostDetachFromHost(safeRouter, newState, isPush)
    }

  private val detachCallback: RouterNavigator.DetachCallback<R, StateT> by lazy {
    RouterDestroyerCallbackWrapper(
      baseCallback = wrapDetachTransitionIfNeed(detachTransition),
      onDestroy = {
        routerAccessor.safeConditionalDestroy(state) { !forceRouterCaching && !state.isCacheable() }
      },
    )
  }

  private fun wrapDetachTransitionIfNeed(
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ): RouterNavigator.DetachCallback<R, StateT>? {
    return (detachTransition as? RouterNavigator.DetachCallback)
      ?: detachTransition?.let { DetachCallbackWrapper(it) }
  }

  /**
   * Wrapper class to wrap [transitionCallback] calls into the new [RouterNavigator.DetachCallback]
   * format.
   */
  private inner class DetachCallbackWrapper(
    private val transitionCallback: RouterNavigator.DetachTransition<R, StateT>,
  ) : RouterNavigator.DetachCallback<R, StateT>() {

    override fun willDetachFromHost(
      router: R,
      previousState: StateT,
      newState: StateT?,
      isPush: Boolean,
    ) = transitionCallback.willDetachFromHost(router, previousState, newState, isPush)
  }

  /**
   * Wrapper class to wrap [RouterNavigator.DetachCallback] and call [onDestroy] after
   * [RouterNavigator.DetachCallback.onPostDetachFromHost]
   */
  private inner class RouterDestroyerCallbackWrapper(
    private val baseCallback: RouterNavigator.DetachCallback<R, StateT>?,
    private val onDestroy: () -> Unit,
  ) : RouterNavigator.DetachCallback<R, StateT>() {

    override fun willDetachFromHost(
      router: R,
      previousState: StateT,
      newState: StateT?,
      isPush: Boolean,
    ) {
      baseCallback?.willDetachFromHost(router, previousState, newState, isPush)
    }

    override fun onPostDetachFromHost(router: R, newState: StateT?, isPush: Boolean) {
      baseCallback?.onPostDetachFromHost(router, newState, isPush)
      onDestroy.invoke()
    }
  }

  private class SafeRouterAccessor<R : Router<*>>(
    private val routerBuilder: () -> R,
  ) {
    private val lock = ReentrantLock()
    private var _router: R? = null

    val router: R
      get() =
        lock.withLock {
          _router
            ?: routerBuilder().let { newRouter ->
              log("Router ${newRouter.javaClass.simpleName} was created")
              _router = newRouter
              newRouter
            }
        }

    fun safeOperation(operation: (R) -> Unit) = lock.withLock { operation(router) }

    fun safeConditionalDestroy(state: RouterNavigatorState, condition: () -> Boolean) =
      lock.withLock {
        if (condition.invoke()) {
          _router?.javaClass?.simpleName?.let { routerName ->
            log("Destroying router $routerName was destroyed")
            _router = null
          }
            ?: run { log("Router of ${state.stateName()} state already destroyed") }
        }
      }
  }

  companion object {
    /** Writes out to the debug log. */
    private fun log(text: String) {
      Rib.getConfiguration().handleDebugMessage("%s: $text", "RouterNavigator")
    }
  }
}
