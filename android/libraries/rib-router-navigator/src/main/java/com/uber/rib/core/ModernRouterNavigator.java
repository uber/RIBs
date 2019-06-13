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
import java.util.Locale;

/**
 * Simple utility for switching a child router based on a state.
 *
 * @param <StateT> type of state to switch on.
 */
public class ModernRouterNavigator<StateT extends RouterNavigatorState>
    implements RouterNavigator<StateT> {
  private final ArrayDeque<RouterAndState<StateT>> navigationStack = new ArrayDeque<>();

  private final Router<?, ?> hostRouter;
  private final String hostRouterName;

  @Nullable private RouterAndState<StateT> currentTransientRouterAndState;

  /**
   * Constructor.
   *
   * @param hostRouter to add and remove children to.
   */
  public ModernRouterNavigator(Router<?, ?> hostRouter) {
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
  public <R extends Router> void pushRetainedState(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition) {
    pushInternal(newState, attachTransition, detachTransition, false);
  }

  @Override
  public <R extends Router> void pushRetainedState(
      StateT newState, AttachTransition<R, StateT> attachTransition) {
    pushRetainedState(newState, attachTransition, null);
  }

  @Override
  public <R extends Router> void pushTransientState(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition) {
    pushInternal(newState, attachTransition, detachTransition, true);
  }

  @Override
  public <R extends Router> void pushTransientState(
      StateT newState, AttachTransition<R, StateT> attachTransition) {
    pushTransientState(newState, attachTransition, null);
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
    int stackSize = currentTransientRouterAndState == null ? 0 : 1;
    return navigationStack.size() + stackSize;
  }

  @Override
  public void hostWillDetach() {
    log(
        String.format(
            Locale.getDefault(), "Detaching RouterNavigator from host -> %s", hostRouterName));
    RouterAndState<StateT> currentRouterAndState = peekCurrentRouterAndState();
    detachInternal(currentRouterAndState, (StateT) null, false);
    currentTransientRouterAndState = null;
    navigationStack.clear();
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

  @Nullable
  private StateT peekCurrentState() {
    RouterAndState<StateT> currentRouterAndState = peekCurrentRouterAndState();
    if (currentRouterAndState != null) {
      return currentRouterAndState.getState();
    } else {
      return null;
    }
  }

  /**
   * Handles the pushing logic.
   *
   * @param newState New state
   * @param attachTransition Transition to use during attach.
   * @param detachTransition Detach transition to use during pop.
   * @param isTransient True if this is a transient entry.
   * @param <R> Router type.
   */
  private <R extends Router> void pushInternal(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition,
      final boolean isTransient) {
    RouterAndState<StateT> fromRouterAndState = peekCurrentRouterAndState();
    StateT fromState = peekCurrentState();
    if (fromState == null || !fromState.name().equals(newState.name())) {
      if (fromRouterAndState != null) {
        detachInternal(fromRouterAndState, newState, true);
      }
      currentTransientRouterAndState = null;

      R newRouter = attachTransition.buildRouter();
      log(String.format(Locale.getDefault(), "Built new router - %s", newRouter));
      attachTransition.willAttachToHost(newRouter, fromState, newState, true);
      String newRouterName = newRouter.getClass().getSimpleName();
      log(String.format(Locale.getDefault(), "Calling willAttachToHost for %s", newRouterName));

      RouterAndState<StateT> routerAndState =
          new RouterAndState<>(newRouter, newState, attachTransition, detachTransition);

      if (isTransient) {
        currentTransientRouterAndState = routerAndState;
      } else {
        navigationStack.push(routerAndState);
      }
      log(
          String.format(
              Locale.getDefault(), "Attaching %s as a child of %s", newRouterName, hostRouterName));
      hostRouter.attachChild(newRouter);
    }
  }

  @Nullable
  private RouterAndState<StateT> peekCurrentRouterAndState() {
    if (currentTransientRouterAndState != null) {
      return currentTransientRouterAndState;
    } else if (!navigationStack.isEmpty()) {
      return navigationStack.peek();
    } else {
      return null;
    }
  }

  /** Writes out to the debug log. */
  private static void log(final String text) {
    Rib.getConfiguration().handleDebugMessage("%s: " + text, "RouterNavigator");
  }
}
