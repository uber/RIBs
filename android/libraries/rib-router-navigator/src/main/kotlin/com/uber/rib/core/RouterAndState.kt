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
  @get:VisibleForTesting val forceRouterCaching: Boolean = false
) {
  @get:Synchronized
  @set:Synchronized
  private var _router: R? = null

  /**
   * Gets or creates the [Router] associated with this state. Router will be destroyed after
   * [RouterNavigator.DetachCallback.onPostDetachFromHost] and if [StateT] is cacheable.
   *
   * @return [Router]
   */
  internal val router: R
    @Synchronized
    get() =
      _router
        ?: attachTransition.buildRouter().apply {
          log("Router ${this@apply.javaClass.simpleName} was created")
          _router = this@apply
        }

  internal fun willAttachToHost(previousState: StateT?, isPush: Boolean) =
    attachTransition.willAttachToHost(router, previousState, state, isPush)

  internal fun willDetachFromHost(
    newState: StateT?,
    isPush: Boolean
  ) = detachCallback.willDetachFromHost(router, state, newState, isPush)

  internal fun onPostDetachFromHost(newState: StateT?, isPush: Boolean) =
    detachCallback.onPostDetachFromHost(router, newState, isPush)

  /**
   * Gets the [RouterNavigator.DetachCallback] associated with this state.
   *
   * @return [RouterNavigator.DetachCallback]
   */
  private val detachCallback: RouterNavigator.DetachCallback<R, StateT> by lazy {
    RouterDestroyerCallbackWrapper(
      baseCallback = wrapDetachTransitionIfNeed(detachTransition),
      onDestroy = ::destroyRouterIfNeed
    )
  }

  private fun wrapDetachTransitionIfNeed(
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?
  ): RouterNavigator.DetachCallback<R, StateT>? {
    return (detachTransition as? RouterNavigator.DetachCallback)
      ?: detachTransition?.let { DetachCallbackWrapper(it) }
  }

  private fun destroyRouterIfNeed() {
    if (!forceRouterCaching && !state.isCacheable()) {
      val routerName = _router?.javaClass?.simpleName
      _router = null
      if (routerName != null) {
        log("Destroying router $routerName was destroyed")
      } else {
        log("Router of ${state.stateName()} state already destroyed")
      }
    }
  }

  /**
   * Wrapper class to wrap [RouterNavigator.DetachTransition] calls into the new
   * [RouterNavigator.DetachCallback] format.
   *
   * @param transitionCallback Base transaction
   */
  private inner class DetachCallbackWrapper(
    private val transitionCallback: RouterNavigator.DetachTransition<R, StateT>
  ) : RouterNavigator.DetachCallback<R, StateT>() {

    override fun willDetachFromHost(
      router: R,
      previousState: StateT,
      newState: StateT?,
      isPush: Boolean
    ) {
      transitionCallback.willDetachFromHost(router, previousState, newState, isPush)
    }
  }

  private inner class RouterDestroyerCallbackWrapper(
    private val baseCallback: RouterNavigator.DetachCallback<R, StateT>?,
    private val onDestroy: () -> Unit
  ) : RouterNavigator.DetachCallback<R, StateT>() {

    override fun willDetachFromHost(
      router: R,
      previousState: StateT,
      newState: StateT?,
      isPush: Boolean
    ) {
      baseCallback?.willDetachFromHost(router, previousState, newState, isPush)
    }

    override fun onPostDetachFromHost(router: R, newState: StateT?, isPush: Boolean) {
      baseCallback?.onPostDetachFromHost(router, newState, isPush)
      onDestroy.invoke()
    }
  }

  companion object {
    /** Writes out to the debug log. */
    private fun log(text: String) {
      Rib.getConfiguration().handleDebugMessage("%s: $text", "RouterNavigator")
    }
  }
}
