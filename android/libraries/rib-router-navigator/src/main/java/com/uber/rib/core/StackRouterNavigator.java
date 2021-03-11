/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Locale;

/**
 * Simple utility for switching a child router based on a state.
 *
 * @param <StateT> type of state to switch on.
 */
public class StackRouterNavigator<StateT extends RouterNavigatorState>
    implements RouterNavigator<StateT> {

  private final ArrayDeque<RouterAndState<StateT>> navigationStack = new ArrayDeque<>();

  private final Router<?> hostRouter;
  private final String hostRouterName;

  @Nullable private RouterAndState<StateT> currentTransientRouterAndState;

  /**
   * Constructor.
   *
   * @param hostRouter to add and remove children to.
   */
  public StackRouterNavigator(Router<?> hostRouter) {
    this.hostRouter = hostRouter;
    this.hostRouterName = hostRouter.getClass().getSimpleName();
    log(
        String.format(
            Locale.getDefault(),
            "Installed new RouterNavigator: Hosting Router -> %s",
            hostRouterName));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void popState() {
    // If we are in a transient state, go ahead and pop that state.
    RouterAndState<StateT> fromState = null;
    if (currentTransientRouterAndState != null) {
      fromState = currentTransientRouterAndState;
      String fromRouterName = fromState.getRouter().getClass().getSimpleName();
      currentTransientRouterAndState = null;
      log(
          String.format(
              Locale.getDefault(),
              "Preparing to pop existing transient state for router: %s",
              fromRouterName));
    } else {
      if (!navigationStack.isEmpty()) {
        fromState = navigationStack.pop();
        String fromRouterName = fromState.getRouter().getClass().getSimpleName();
        log(
            String.format(
                Locale.getDefault(),
                "Preparing to pop existing state for router: %s",
                fromRouterName));
      }
    }

    if (fromState != null) {
      // Pull the incoming state (So we can restore it.)
      RouterAndState<StateT> toState = null;
      if (!navigationStack.isEmpty()) {
        toState = navigationStack.peek();
      }

      detachInternal(fromState, toState, false);

      if (toState != null) {
        attachInternal(fromState, toState, false);
      }
    } else {
      log("No state to pop. No action will be taken.");
    }
  }

  @Override
  public <R extends Router> void pushState(
      StateT newState,
      AttachTransition<R, StateT> attachTransition,
      @Nullable DetachTransition<R, StateT> detachTransition) {
    pushState(newState, Flag.DEFAULT, attachTransition, detachTransition);
  }

  @Override
  public <R extends Router> void pushState(
      StateT newState,
      Flag flag,
      AttachTransition<R, StateT> attachTransition,
      @Nullable DetachTransition<R, StateT> detachTransition) {

    StateT fromState = peekState();
    RouterAndState<StateT> currentRouterAndState = peekCurrentRouterAndState();
    if (fromState != null && !fromState.name().equals(newState.name())) {
      if (currentRouterAndState != null && currentRouterAndState.getRouter() != null) {
        detachInternal(currentRouterAndState, newState, true);
      }
    }

    boolean newStateIsTop = fromState != null && fromState.name().equals(newState.name());
    if (currentTransientRouterAndState != null) {
      if (!(newStateIsTop && flag == Flag.TRANSIENT)) {
        currentTransientRouterAndState = null;
      }
    }

    RouterAndState<StateT> newRouterAndState;
    switch (flag) {
      case DEFAULT:
        if (fromState != null && fromState.name().equals(newState.name())) {
          detachInternal(currentRouterAndState, newState, true);
        }
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition);
        navigationStack.push(newRouterAndState);
        attachInternal(currentRouterAndState, newRouterAndState, true);
        break;
      case TRANSIENT:
        if (newStateIsTop) {
          return;
        }
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition);
        currentTransientRouterAndState = newRouterAndState;
        attachInternal(currentRouterAndState, newRouterAndState, true);
        break;
      case CLEAR_TOP:
        if (newStateIsTop) {
          return;
        }
        clearTop(currentRouterAndState, newState, attachTransition, detachTransition);
        break;
      case SINGLE_TOP:
        if (newStateIsTop) {
          return;
        }
        removeStateFromStack(newState);
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition);
        navigationStack.push(newRouterAndState);
        attachInternal(currentRouterAndState, newRouterAndState, true);
        break;
      case REORDER_TO_TOP:
        if (newStateIsTop) {
          return;
        }
        reorderTop(currentRouterAndState, newState, attachTransition, detachTransition);
        break;
      case NEW_TASK:
        if (currentRouterAndState != null && newStateIsTop) {
          navigationStack.clear();
          navigationStack.push(currentRouterAndState);
        } else {
          detachAll();
          newRouterAndState = buildNewState(newState, attachTransition, detachTransition);
          attachInternal(currentRouterAndState, newRouterAndState, true);
          navigationStack.push(newRouterAndState);
        }

        break;
      case REPLACE_TOP:
        if (!navigationStack.isEmpty()) {
          navigationStack.pop();
        }
        newRouterAndState = buildNewState(newState, attachTransition, detachTransition);
        navigationStack.push(newRouterAndState);
        attachInternal(currentRouterAndState, newRouterAndState, true);
        break;
    }
  }

  @Nullable
  @Override
  public Router peekRouter() {
    RouterAndState<StateT> top = peekCurrentRouterAndState();
    if (top == null) {
      return null;
    }
    return top.getRouter();
  }

  @Nullable
  @Override
  public StateT peekState() {
    RouterAndState<StateT> top = peekCurrentRouterAndState();
    if (top == null) {
      return null;
    }
    return top.getState();
  }

  @IntRange(from = 0)
  @Override
  public int size() {
    return navigationStack.size();
  }

  /**
   * This will pop the current active router and clear the entire stack.
   *
   * <p>NOTE: This must be called when host interactor is going to detach.
   */
  public void detachAll() {
    log(
        String.format(
            Locale.getDefault(), "Detaching RouterNavigator from host -> %s", hostRouterName));
    RouterAndState<StateT> currentRouterAndState = peekCurrentRouterAndState();
    detachInternal(currentRouterAndState, (StateT) null, false);
    currentTransientRouterAndState = null;
    navigationStack.clear();
  }

  private RouterAndState<StateT> buildNewState(
      StateT newState,
      AttachTransition<? extends Router, StateT> attachTransition,
      @Nullable DetachTransition<? extends Router, StateT> detachTransition) {
    Router newRouter = attachTransition.buildRouter();
    RouterAndState<StateT> newRouterAndState =
        new RouterAndState<>(newRouter, newState, attachTransition, detachTransition);
    return newRouterAndState;
  }

  /**
   * Handles the attachment logic for a router.
   *
   * @param fromRouterState From router state.
   * @param toRouterState New state.
   * @param isPush True if this is from a push.
   */
  @SuppressWarnings("unchecked")
  private void attachInternal(
      @Nullable final RouterAndState<StateT> fromRouterState,
      final RouterAndState<StateT> toRouterState,
      final boolean isPush) {
    String toRouterName = toRouterState.getRouter().getClass().getSimpleName();
    AttachTransition<Router, StateT> attachCallback = toRouterState.getAttachTransition();

    log(String.format(Locale.getDefault(), "Calling willAttachToHost for %s", toRouterName));
    RouterNavigatorEvents.getInstance()
        .emitEvent(
            RouterNavigatorEventType.WILL_ATTACH_TO_HOST, hostRouter, toRouterState.getRouter());
    attachCallback.willAttachToHost(
        toRouterState.getRouter(),
        fromRouterState == null ? null : fromRouterState.getState(),
        toRouterState.getState(),
        isPush);
    log(
        String.format(
            Locale.getDefault(), "Attaching %s as a child of %s", toRouterName, hostRouterName));
    hostRouter.attachChild(toRouterState.getRouter());
  }

  /**
   * Handles the detachment of a router.
   *
   * @param fromRouterState Previous state
   * @param toRouterState New state
   * @param isPush True if this is caused by a push
   */
  private void detachInternal(
      @Nullable final RouterAndState<StateT> fromRouterState,
      @Nullable final RouterAndState<StateT> toRouterState,
      final boolean isPush) {
    detachInternal(
        fromRouterState, toRouterState != null ? toRouterState.getState() : null, isPush);
  }

  /**
   * Handles the detachment of a router.
   *
   * @param fromRouterState Previous state
   * @param toState New state
   * @param isPush True if this is caused by a push
   */
  @SuppressWarnings("unchecked")
  private void detachInternal(
      @Nullable final RouterAndState<StateT> fromRouterState,
      @Nullable final StateT toState,
      final boolean isPush) {
    if (fromRouterState == null) {
      log("No router to transition from. Call to detach will be dropped.");
      return;
    }

    DetachCallback<Router, StateT> callback = fromRouterState.getDetachCallback();
    String fromRouterName = fromRouterState.getRouter().getClass().getSimpleName();

    if (callback != null) {
      log(String.format(Locale.getDefault(), "Calling willDetachFromHost for %s", fromRouterName));
      callback.willDetachFromHost(
          fromRouterState.getRouter(), fromRouterState.getState(), toState, isPush);
    }
    log(String.format(Locale.getDefault(), "Detaching %s from %s", fromRouterName, hostRouterName));
    hostRouter.detachChild(fromRouterState.getRouter());
    if (callback != null) {
      log(
          String.format(
              Locale.getDefault(), "Calling onPostDetachFromHost for %s", fromRouterName));
      callback.onPostDetachFromHost(fromRouterState.getRouter(), toState, isPush);
    }
  }

  @Nullable
  private RouterAndState<StateT> peekCurrentRouterAndState() {
    if (currentTransientRouterAndState != null) {
      return currentTransientRouterAndState;
    } else {
      return navigationStack.peek();
    }
  }

  private void clearTop(
      @Nullable RouterAndState<StateT> currentRouterAndState,
      StateT newState,
      AttachTransition<? extends Router, StateT> attachTransition,
      @Nullable DetachTransition<? extends Router, StateT> detachTransition) {
    boolean found = false;
    Iterator<RouterAndState<StateT>> navigationIterator = navigationStack.iterator();
    while (navigationIterator.hasNext()) {
      if (navigationIterator.next().getState().equals(newState)) {
        found = true;
        break;
      }
    }

    if (found) {
      navigationIterator = navigationStack.iterator();
      while (navigationIterator.hasNext()) {
        RouterAndState<StateT> routerAndState = navigationIterator.next();
        if (routerAndState.getState().equals(newState)) {
          attachInternal(currentRouterAndState, routerAndState, true);
          break;
        } else {
          navigationIterator.remove();
        }
      }
    } else {
      RouterAndState<StateT> newRouterAndState =
          buildNewState(newState, attachTransition, detachTransition);
      navigationStack.push(newRouterAndState);
      attachInternal(currentRouterAndState, newRouterAndState, true);
    }
  }

  private void reorderTop(
      @Nullable RouterAndState<StateT> currentRouterAndState,
      StateT newState,
      AttachTransition<? extends Router, StateT> attachTransition,
      @Nullable DetachTransition<? extends Router, StateT> detachTransition) {
    boolean found = false;
    Iterator<RouterAndState<StateT>> navigationIterator = navigationStack.iterator();
    while (navigationIterator.hasNext()) {
      RouterAndState<StateT> routerAndState = navigationIterator.next();
      if (routerAndState.getState().equals(newState)) {
        navigationIterator.remove();
        navigationStack.push(routerAndState);
        attachInternal(currentRouterAndState, routerAndState, true);
        found = true;
        break;
      }
    }

    if (!found) {
      RouterAndState<StateT> newRouterAndState =
          buildNewState(newState, attachTransition, detachTransition);
      navigationStack.push(newRouterAndState);
      attachInternal(currentRouterAndState, newRouterAndState, true);
    }
  }

  private void removeStateFromStack(StateT state) {
    Iterator<RouterAndState<StateT>> navigationIterator = navigationStack.iterator();
    while (navigationIterator.hasNext()) {
      if (navigationIterator.next().getState().equals(state)) {
        navigationIterator.remove();
      }
    }
  }

  /** Writes out to the debug log. */
  private static void log(final String text) {
    Rib.getConfiguration().handleDebugMessage("%s: " + text, "RouterNavigator");
  }

  /*
  Deprecated methods
   */
  @Override
  @Deprecated
  public <R extends Router> void pushRetainedState(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition) {
    pushState(newState, attachTransition, detachTransition);
  }

  @Override
  @Deprecated
  public <R extends Router> void pushRetainedState(
      StateT newState, AttachTransition<R, StateT> attachTransition) {
    pushState(newState, attachTransition, null);
  }

  @Override
  @Deprecated
  public <R extends Router> void pushTransientState(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition) {
    pushState(newState, Flag.TRANSIENT, attachTransition, detachTransition);
  }

  @Override
  @Deprecated
  public <R extends Router> void pushTransientState(
      StateT newState, AttachTransition<R, StateT> attachTransition) {
    pushState(newState, Flag.TRANSIENT, attachTransition, null);
  }

  @Override
  @Deprecated
  public void hostWillDetach() {
    detachAll();
  }
}
