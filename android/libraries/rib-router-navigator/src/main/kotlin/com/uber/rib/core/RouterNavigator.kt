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
import org.checkerframework.checker.guieffect.qual.PolyUIEffect
import org.checkerframework.checker.guieffect.qual.PolyUIType
import org.checkerframework.checker.guieffect.qual.UIEffect

/**
 * Simple utility for switching a child router based on a state.
 *
 * @param <StateT> type of state to switch on.
 */
public interface RouterNavigator<StateT : RouterNavigatorState> {
  /** Determine how pushes will affect the stack */
  public enum class Flag {
    /** Push a new state to the top of the navigation stack. */
    DEFAULT,

    /** Push a state that will not be retained when the next state is pushed. */
    TRANSIENT,

    /**
     * Start looking at the stack from the top until we see the state that is being pushed. If it is
     * found, remove all states that we traversed and transition the app to the found state. If the
     * state is not found in the stack, create a new instance and transition to it pushing it on top
     * of the stack.
     */
    CLEAR_TOP,

    /**
     * First create a new instance of the state and push it to the top of the stack. Then traverse
     * down the stack and and delete any instances of the state being pushed.
     */
    SINGLE_TOP,

    /**
     * Search through the stack for the state and if found, move the state to the top but don’t
     * change the stack otherwise. If the state doesn’t exist in the stack, create a new instance
     * and push to the top.
     */
    REORDER_TO_TOP,

    /** Clears the previous stack (no back stack) and pushes the state on to the top of the stack */
    NEW_TASK,

    /**
     * Clears the previous stack (no back stack) and pushes the state on to the top of the stack. If
     * the state is already on the top of the stack, it is detached and replaced.
     */
    NEW_TASK_REPLACE,

    /**
     * Remove the top state in the stack if it is not empty and then push a new state to the top of
     * the stack.
     */
    REPLACE_TOP,
  }

  /** Pop the current state and rewind to the previous state (if there is a previous state). */
  public fun popState()

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already.
   *
   * NOTE: This will retain the Riblet in memory (if [RouterNavigatorState.isCacheable] is TRUE)
   * until it is popped or detached by a push with certain flags.
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach. </R>
   */
  public fun <R : Router<*>> pushState(
    newState: StateT,
    attachTransition: AttachTransition<R, StateT>,
    detachTransition: DetachTransition<R, StateT>?,
  )

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already. The transition will be controlled by the [StackRouterNavigator.Flag] provided.
   *
   * NOTE: This will retain the Riblet in memory (if [RouterNavigatorState.isCacheable] is TRUE)
   * until it is popped or detached by a push with certain flags.
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach. </R>
   */
  public fun <R : Router<*>> pushState(
    newState: StateT,
    flag: Flag,
    attachTransition: AttachTransition<R, StateT>,
    detachTransition: DetachTransition<R, StateT>?,
  )

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already.
   *
   * NOTE: This will retain the Riblet in memory (if [RouterNavigatorState.isCacheable] is TRUE)
   * until it is popped. To push transient, riblets, use [RouterNavigator.pushTransientState]
   *
   * Deprecated: Use pushState(newState, attachTransition, detachTransition)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach. </R>
   */
  @Deprecated("")
  public fun <R : Router<*>> pushRetainedState(
    newState: StateT,
    attachTransition: AttachTransition<R, StateT>,
    detachTransition: DetachTransition<R, StateT>?,
  )

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already.
   *
   * NOTE: This will retain the Riblet in memory (if [RouterNavigatorState.isCacheable] is TRUE)
   * until it is popped. To push transient, riblets, use [RouterNavigator.pushTransientState]
   *
   * Deprecated: Use pushState(newState, attachTransition, null)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param <R> [Router] type. </R>
   */
  @Deprecated("")
  public fun <R : Router<*>> pushRetainedState(
    newState: StateT,
    attachTransition: AttachTransition<R, StateT>,
  )

  /**
   * Switch to a new transient state - this will switch out children if the state is not the current
   * active state already.
   *
   * NOTE: Transient states do not live in the back navigation stack.
   *
   * Deprecated: Use pushState(newState, Flag.TRANSIENT, attachTransition, detachTransition)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach. </R>
   */
  @Deprecated("")
  public fun <R : Router<*>> pushTransientState(
    newState: StateT,
    attachTransition: AttachTransition<R, StateT>,
    detachTransition: DetachTransition<R, StateT>?,
  )

  /**
   * Switch to a new transient state - this will switch out children if the state is not the current
   * active state already.
   *
   * NOTE: Transient states do not live in the back navigation stack.
   *
   * Deprecated: Use pushState(newState, Flag.TRANSIENT, attachTransition, null)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param <R> [Router] type. </R>
   */
  @Deprecated("")
  public fun <R : Router<*>> pushTransientState(
    newState: StateT,
    attachTransition: AttachTransition<R, StateT>,
  )

  /**
   * Peek the top [Router] on the stack.
   *
   * @return the top [Router] on the stack.
   */
  public fun peekRouter(): Router<*>?

  /**
   * Peek the top [StateT] on the stack.
   *
   * @return the top [StateT] on the stack.
   */
  public fun peekState(): StateT?

  /**
   * Gets the size of the navigation stack.
   *
   * @return Size of the navigation stack.
   */
  @IntRange(from = 0) public fun size(): Int

  /**
   * Must be called when host interactor is going to detach. This will pop the current active router
   * and clear the entire stack.
   */
  public fun hostWillDetach()

  /**
   * Allows consumers to write custom attachment logic when switching states.
   *
   * @param <StateT> state type. </StateT>
   */
  public interface AttachTransition<RouterT : Router<*>, StateT : RouterNavigatorState> {
    /**
     * Constructs a new [RouterT] instance. This will only be called once.
     *
     * @return the newly attached child router.
     */
    public fun buildRouter(): RouterT

    /**
     * Prepares the router for a state transition. [StackRouterNavigator] will handling attaching
     * the router, but consumers of this should handle adding any views.
     *
     * @param router [RouterT] that is being attached.
     * @param previousState state the navigator is transition from (if any).
     * @param newState state the navigator is transitioning to.
     */
    @UIEffect
    public fun willAttachToHost(
      router: RouterT,
      previousState: StateT?,
      newState: StateT,
      isPush: Boolean,
    )
  }

  /**
   * Allows consumers to write custom detachment logic when the state is changing. This allows for
   * custom state prior to and immediately post detach.
   *
   * @param <RouterT> [RouterT]
   * @param <StateT> [StateT] </StateT></RouterT>
   */
  public abstract class DetachCallback<RouterT : Router<*>, StateT : RouterNavigatorState> :
    DetachTransition<RouterT, StateT> {
    override fun willDetachFromHost(
      router: RouterT,
      previousState: StateT,
      newState: StateT?,
      isPush: Boolean,
    ) {}

    /**
     * Notifies the consumer that the [StackRouterNavigator] has detached the supplied [ ].
     * Consumers can complete any post detachment behavior here.
     *
     * @param router [Router]
     * @param newState [StateT]
     */
    public open fun onPostDetachFromHost(router: RouterT, newState: StateT?, isPush: Boolean) {}
  }

  /**
   * Allows consumers to write custom detachment logic wen switching states.
   *
   * @param <RouterT> router type to detach.
   * @param <StateT> state type. </StateT></RouterT>
   */
  @PolyUIType
  public interface DetachTransition<RouterT : Router<*>, StateT : RouterNavigatorState> {
    /**
     * Notifies consumer that [StackRouterNavigator] is going to detach this router. Consumers
     * should remove any views and perform any required cleanup.
     *
     * @param router being removed.
     * @param previousState state the navigator is transitioning out of.
     * @param newState state the navigator is transition in to (if any).
     */
    @PolyUIEffect
    public fun willDetachFromHost(
      router: RouterT,
      previousState: StateT,
      newState: StateT?,
      isPush: Boolean,
    )
  }
}
