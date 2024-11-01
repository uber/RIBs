/*
 * Copyright (C) 2017. Uber Technologies
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

import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import com.uber.rib.core.RouterNavigatorEvents.Companion.instance
import java.util.ArrayDeque
import java.util.Locale

/**
 * Simple utility for switching a child router based on a state.
 *
 * @param hostRouter Host router
 * @param forceRouterCaching Override [RouterNavigatorState.isCacheable] behavior
 */
public open class StackRouterNavigator<StateT : RouterNavigatorState>
@JvmOverloads
constructor(
  private val hostRouter: Router<*>,
  // Enabled by default for safe migration to newer lib version
  private val forceRouterCaching: Boolean = true,
) : RouterNavigator<StateT> {

  @VisibleForTesting internal val navigationStack = ArrayDeque<RouterAndState<*, StateT>>()
  private val hostRouterName: String = hostRouter.javaClass.simpleName
  private var currentTransientRouterAndState: RouterAndState<*, StateT>? = null

  override fun popState() {
    // If we are in a transient state, go ahead and pop that state.
    var fromState: RouterAndState<*, StateT>? = null
    if (currentTransientRouterAndState != null) {
      fromState = currentTransientRouterAndState
      val fromRouterName = fromState?.router?.javaClass?.simpleName
      currentTransientRouterAndState = null
      log(
        String.format(
          Locale.getDefault(),
          "Preparing to pop existing transient state for router: %s",
          fromRouterName,
        ),
      )
    } else {
      if (!navigationStack.isEmpty()) {
        fromState = navigationStack.pop()
        val fromRouterName: String = fromState.router.javaClass.simpleName
        log(
          String.format(
            Locale.getDefault(),
            "Preparing to pop existing state for router: %s",
            fromRouterName,
          ),
        )
      }
    }
    if (fromState != null) {
      // Pull the incoming state (So we can restore it.)
      var toState: RouterAndState<*, StateT>? = null
      if (!navigationStack.isEmpty()) {
        toState = navigationStack.peek()
      }
      detachInternal(fromState, toState?.state, false)
      if (toState != null) {
        attachInternal(fromState, toState, false)
      }
    } else {
      log("No state to pop. No action will be taken.")
    }
  }

  override fun <R : Router<*>> pushState(
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ) {
    pushState(newState, RouterNavigator.Flag.DEFAULT, attachTransition, detachTransition)
  }

  override fun <R : Router<*>> pushState(
    newState: StateT,
    flag: RouterNavigator.Flag,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ) {
    val fromState = peekState()
    val currentRouterAndState = peekCurrentRouterAndState()
    if (fromState != null && fromState.stateName() != newState.stateName()) {
      if (currentRouterAndState?.router != null) {
        detachInternal(currentRouterAndState, newState, true)
      }
    }
    val newStateIsTop = fromState != null && fromState.stateName() == newState.stateName()
    if (currentTransientRouterAndState != null) {
      if (!(newStateIsTop && flag == RouterNavigator.Flag.TRANSIENT)) {
        currentTransientRouterAndState = null
      }
    }
    val newRouterAndState: RouterAndState<*, StateT>
    when (flag) {
      RouterNavigator.Flag.DEFAULT -> {
        if (newStateIsTop) {
          detachInternal(currentRouterAndState, newState, true)
        }
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
        navigationStack.push(newRouterAndState)
        attachInternal(currentRouterAndState, newRouterAndState, true)
      }
      RouterNavigator.Flag.TRANSIENT -> {
        if (newStateIsTop) {
          return
        }
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
        currentTransientRouterAndState = newRouterAndState
        attachInternal(currentRouterAndState, newRouterAndState, true)
      }
      RouterNavigator.Flag.CLEAR_TOP -> {
        if (newStateIsTop) {
          return
        }
        clearTop(currentRouterAndState, newState, attachTransition, detachTransition)
      }
      RouterNavigator.Flag.SINGLE_TOP -> {
        if (newStateIsTop) {
          return
        }
        removeStateFromStack(newState)
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
        navigationStack.push(newRouterAndState)
        attachInternal(currentRouterAndState, newRouterAndState, true)
      }
      RouterNavigator.Flag.REORDER_TO_TOP -> {
        if (newStateIsTop) {
          return
        }
        reorderTop(currentRouterAndState, newState, attachTransition, detachTransition)
      }
      RouterNavigator.Flag.NEW_TASK ->
        if (currentRouterAndState != null && newStateIsTop) {
          navigationStack.clear()
          navigationStack.push(currentRouterAndState)
        } else {
          detachAll()
          newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
          attachInternal(currentRouterAndState, newRouterAndState, true)
          navigationStack.push(newRouterAndState)
        }
      RouterNavigator.Flag.NEW_TASK_REPLACE -> {
        detachAll()
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
        attachInternal(currentRouterAndState, newRouterAndState, true)
        navigationStack.push(newRouterAndState)
      }
      RouterNavigator.Flag.REPLACE_TOP -> {
        if (!navigationStack.isEmpty()) {
          navigationStack.pop()
        }
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
        navigationStack.push(newRouterAndState)
        attachInternal(currentRouterAndState, newRouterAndState, true)
      }
    }
  }

  override fun peekRouter(): Router<*>? {
    val top = peekCurrentRouterAndState() ?: return null
    return top.router
  }

  override fun peekState(): StateT? {
    val top = peekCurrentRouterAndState() ?: return null
    return top.state
  }

  @IntRange(from = 0)
  override fun size(): Int {
    return navigationStack.size
  }

  /**
   * This will pop the current active router and clear the entire stack.
   *
   * NOTE: This must be called when host interactor is going to detach.
   */
  public open fun detachAll() {
    log(
      String.format(
        Locale.getDefault(),
        "Detaching RouterNavigator from host -> %s",
        hostRouterName,
      ),
    )
    val currentRouterAndState = peekCurrentRouterAndState()
    detachInternal(currentRouterAndState, null as StateT?, false)
    currentTransientRouterAndState = null
    navigationStack.clear()
  }

  private fun <R : Router<*>> buildNewState(
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ): RouterAndState<R, StateT> {
    return RouterAndState(
      newState,
      attachTransition,
      detachTransition,
      forceRouterCaching = forceRouterCaching,
    )
  }

  /**
   * Handles the attachment logic for a router.
   *
   * @param fromRouterState From router state.
   * @param toRouterState New state.
   * @param isPush True if this is from a push.
   */
  private fun attachInternal(
    fromRouterState: RouterAndState<*, StateT>?,
    toRouterState: RouterAndState<*, StateT>,
    isPush: Boolean,
  ) {
    val toRouterName: String = toRouterState.router.javaClass.simpleName
    log(String.format(Locale.getDefault(), "Calling willAttachToHost for %s", toRouterName))
    instance.emitEvent(
      RouterNavigatorEventType.WILL_ATTACH_TO_HOST,
      hostRouter,
      toRouterState.router,
    )
    toRouterState.willAttachToHost(fromRouterState?.state, isPush)
    log(
      String.format(
        Locale.getDefault(),
        "Attaching %s as a child of %s",
        toRouterName,
        hostRouterName,
      ),
    )
    hostRouter.attachChild(toRouterState.router)
  }

  /**
   * Handles the detachment of a router.
   *
   * @param fromRouterState Previous state
   * @param toState New state
   * @param isPush True if this is caused by a push
   */
  private fun detachInternal(
    fromRouterState: RouterAndState<*, StateT>?,
    toState: StateT?,
    isPush: Boolean,
  ) {
    if (fromRouterState == null) {
      log("No router to transition from. Call to detach will be dropped.")
      return
    }
    val fromRouterName: String = fromRouterState.router.javaClass.simpleName
    log(String.format(Locale.getDefault(), "Calling willDetachFromHost for %s", fromRouterName))
    fromRouterState.willDetachFromHost(toState, isPush)

    log(String.format(Locale.getDefault(), "Detaching %s from %s", fromRouterName, hostRouterName))
    hostRouter.detachChild(fromRouterState.router)

    log(String.format(Locale.getDefault(), "Calling onPostDetachFromHost for %s", fromRouterName))
    fromRouterState.onPostDetachFromHost(toState, isPush)
  }

  private fun peekCurrentRouterAndState(): RouterAndState<*, StateT>? {
    return if (currentTransientRouterAndState != null) {
      currentTransientRouterAndState
    } else {
      navigationStack.peek()
    }
  }

  private fun <R : Router<*>> clearTop(
    currentRouterAndState: RouterAndState<*, StateT>?,
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ) {
    var found = false
    var navigationIterator = navigationStack.iterator()
    while (navigationIterator.hasNext()) {
      if (navigationIterator.next().state == newState) {
        found = true
        break
      }
    }
    if (found) {
      navigationIterator = navigationStack.iterator()
      while (navigationIterator.hasNext()) {
        val routerAndState = navigationIterator.next()
        if (routerAndState.state == newState) {
          attachInternal(currentRouterAndState, routerAndState, true)
          break
        } else {
          navigationIterator.remove()
        }
      }
    } else {
      val newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
      navigationStack.push(newRouterAndState)
      attachInternal(currentRouterAndState, newRouterAndState, true)
    }
  }

  private fun <R : Router<*>> reorderTop(
    currentRouterAndState: RouterAndState<*, StateT>?,
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ) {
    var found = false
    val navigationIterator = navigationStack.iterator()
    while (navigationIterator.hasNext()) {
      val routerAndState = navigationIterator.next()
      if (routerAndState.state == newState) {
        navigationIterator.remove()
        navigationStack.push(routerAndState)
        attachInternal(currentRouterAndState, routerAndState, true)
        found = true
        break
      }
    }
    if (!found) {
      val newRouterAndState = buildNewState(newState, attachTransition, detachTransition)
      navigationStack.push(newRouterAndState)
      attachInternal(currentRouterAndState, newRouterAndState, true)
    }
  }

  private fun removeStateFromStack(state: StateT) {
    val navigationIterator = navigationStack.iterator()
    while (navigationIterator.hasNext()) {
      if (navigationIterator.next().state == state) {
        navigationIterator.remove()
      }
    }
  }

  /*
  Deprecated methods
   */
  @Deprecated("")
  override fun <R : Router<*>> pushRetainedState(
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ) {
    pushState(newState, attachTransition, detachTransition)
  }

  @Deprecated("")
  override fun <R : Router<*>> pushRetainedState(
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
  ) {
    pushState(newState, attachTransition, null)
  }

  @Deprecated("")
  override fun <R : Router<*>> pushTransientState(
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
    detachTransition: RouterNavigator.DetachTransition<R, StateT>?,
  ) {
    pushState(newState, RouterNavigator.Flag.TRANSIENT, attachTransition, detachTransition)
  }

  @Deprecated("")
  override fun <R : Router<*>> pushTransientState(
    newState: StateT,
    attachTransition: RouterNavigator.AttachTransition<R, StateT>,
  ) {
    pushState(newState, RouterNavigator.Flag.TRANSIENT, attachTransition, null)
  }

  @Deprecated("")
  override fun hostWillDetach() {
    detachAll()
  }

  public companion object {
    /** Writes out to the debug log. */
    private fun log(text: String) {
      Rib.getConfiguration().handleDebugMessage("%s: $text", "RouterNavigator")
    }
  }

  /**
   * Constructor.
   *
   * @param hostRouter to add and remove children to.
   */
  init {
    log(
      String.format(
        Locale.getDefault(),
        "Installed new RouterNavigator: Hosting Router -> %s",
        hostRouterName,
      ),
    )
  }
}
