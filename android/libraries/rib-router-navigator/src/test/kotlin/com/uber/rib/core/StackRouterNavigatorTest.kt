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

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class StackRouterNavigatorTest {
  private enum class TestState : RouterNavigatorState {
    STATE_1,
    STATE_2,
    STATE_3
  }

  private val hostRouter: Router<*> = mock()
  private val router1: Router<*> = mock()
  private val router2: Router<*> = mock()
  private val router3: Router<*> = mock()

  private val attachTransition1: RouterNavigator.AttachTransition<Router<*>, TestState> = mock {
    on { buildRouter() } doReturn router1
  }
  private val attachTransition2: RouterNavigator.AttachTransition<Router<*>, TestState> = mock {
    on { buildRouter() } doReturn router2
  }
  private val attachTransition3: RouterNavigator.AttachTransition<Router<*>, TestState> = mock {
    on { buildRouter() } doReturn router3
  }

  private val detachTransition1: RouterNavigator.DetachTransition<Router<*>, TestState> = mock()
  private val detachTransition2: RouterNavigator.DetachTransition<Router<*>, TestState> = mock()
  private val detachTransition3: RouterNavigator.DetachTransition<Router<*>, TestState> = mock()

  private lateinit var routerNavigator: StackRouterNavigator<TestState>

  @Before
  fun setup() {
    routerNavigator = StackRouterNavigator(hostRouter)
  }

  @Test
  fun hostWillDetach_whenThereIsAnAttachedRouter_shouldRunDetachRunner() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.hostWillDetach()
    verify(detachTransition1).willDetachFromHost(router1, TestState.STATE_1, null, false)
  }

  @Test
  fun hostWillDetach_whenThereIsAnAttachedRouter_andAttachesAgain_shouldReattach() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.hostWillDetach()
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1, times(2)).willAttachToHost(router1, null, TestState.STATE_1, true)
  }

  @Test
  fun pushRetained_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(hostRouter).detachChild(router1)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verifyNoInteractions(detachTransition2)
  }

  @Test
  fun pushTransientDeprecated_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushTransientState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(hostRouter).detachChild(router1)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verifyNoInteractions(detachTransition2)
  }

  @Test
  fun pushRetained_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    val hostRouter: Router<*> = mock {
      on { attachChild(router2) } doAnswer { routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3) }
    }
    routerNavigator = StackRouterNavigator(hostRouter)
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushTransientDeprecated_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    val hostRouter: Router<*> = mock {
      on { attachChild(router2) } doAnswer { routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3) }
    }
    routerNavigator = StackRouterNavigator(hostRouter)
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushTransientState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushRetained_overACurrentTransientRouter_shouldPopTheTransientRouter() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    routerNavigator.pushTransientState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    routerNavigator.pushRetainedState(TestState.STATE_3, attachTransition3, detachTransition3)
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true)
    routerNavigator.popState()
    verify(detachTransition3)
      .willDetachFromHost(router3, TestState.STATE_3, TestState.STATE_1, false)
    verify(attachTransition1)
      .willAttachToHost(router1, TestState.STATE_3, TestState.STATE_1, false)
  }

  @Test
  fun detachAll_whenThereIsAnAttachedRouter_shouldRunDetachRunner() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.detachAll()
    verify(detachTransition1).willDetachFromHost(router1, TestState.STATE_1, null, false)
  }

  @Test
  fun detachAll_whenThereIsAnAttachedRouter_andAttachesAgain_shouldReattach() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.detachAll()
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1, times(2)).willAttachToHost(router1, null, TestState.STATE_1, true)
  }

  @Test
  fun pushDefault_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(hostRouter).detachChild(router1)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verifyNoInteractions(detachTransition2)
  }

  @Test
  fun pushTransient_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushState(
      TestState.STATE_2,
      RouterNavigator.Flag.TRANSIENT,
      attachTransition2,
      detachTransition2
    )
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(hostRouter).detachChild(router1)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verifyNoInteractions(detachTransition2)
  }

  @Test
  fun pushDefault_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    val hostRouter: Router<*> = mock {
      on { attachChild(router2) } doAnswer { routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3) }
    }
    routerNavigator = StackRouterNavigator(hostRouter)
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushTransient_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    val hostRouter: Router<*> = mock {
      on { attachChild(router2) } doAnswer { routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3) }
    }
    routerNavigator = StackRouterNavigator(hostRouter)
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    verify(hostRouter).attachChild(router1)
    verifyNoInteractions(detachTransition1)
    routerNavigator.pushState(
      TestState.STATE_2,
      RouterNavigator.Flag.TRANSIENT,
      attachTransition2,
      detachTransition2
    )
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true)
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushDefault_overACurrentTransientRouter_shouldPopTheTransientRouter() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true)
    routerNavigator.pushState(
      TestState.STATE_2,
      RouterNavigator.Flag.TRANSIENT,
      attachTransition2,
      detachTransition2
    )
    verify(detachTransition1)
      .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true)
    routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3)
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true)
    routerNavigator.popState()
    verify(detachTransition3)
      .willDetachFromHost(router3, TestState.STATE_3, TestState.STATE_1, false)
    verify(attachTransition1)
      .willAttachToHost(router1, TestState.STATE_3, TestState.STATE_1, false)
  }

  @Test
  fun pushClearTop_whenNotInStack_shouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.CLEAR_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
  }

  @Test
  fun pushClearTop_whenInStack_shouldRemoveStatesToThatState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.CLEAR_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushClearTop_whenTopOfStack_shouldNotReorderOrAttach() {
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.CLEAR_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushSingleTop_whenNotInStack_shouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.SINGLE_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
  }

  @Test
  fun pushSingleTop_whenInStack_shouldPushStateToTopAndRemoveOtherInstances() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.SINGLE_TOP,
      attachTransition3,
      detachTransition3
    )
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_1, true)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
  }

  @Test
  fun pushSingleTop_whenTopOfStack_shouldNotReorderStack() {
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.SINGLE_TOP,
      attachTransition3,
      detachTransition3
    )
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_2, TestState.STATE_1, true)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
  }

  @Test
  fun pushReorderTop_whenNotInStack_shouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.REORDER_TO_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
  }

  @Test
  fun pushReorderTop_whenInStack_shouldRemoveStatesToThatState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.REORDER_TO_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushReorderTop_whenTopOfStackStack_shouldNotReorderStack() {
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.REORDER_TO_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true)
    verify(attachTransition3, never())
      .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true)
  }

  @Test
  fun pushNewTask_detachesCurrentAndClearsCurrentStack_andShouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.NEW_TASK,
      attachTransition3,
      detachTransition3
    )
    verify(detachTransition2).willDetachFromHost(router2, TestState.STATE_2, null, false)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3)
    Truth.assertThat(routerNavigator.size()).isEqualTo(1)
  }

  @Test
  fun pushNewTask_whenCurrentTopIsNewState_onlyClearsTheBackStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_2,
      RouterNavigator.Flag.NEW_TASK,
      attachTransition3,
      detachTransition3
    )
    verifyNoInteractions(detachTransition2)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    Truth.assertThat(routerNavigator.size()).isEqualTo(1)
  }

  @Test
  fun pushNewTaskReplace_detachesCurrentAndClearsCurrentStack_andShouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.NEW_TASK_REPLACE,
      attachTransition3,
      detachTransition3
    )
    verify(detachTransition2).willDetachFromHost(router2, TestState.STATE_2, null, false)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3)
    Truth.assertThat(routerNavigator.size()).isEqualTo(1)
  }

  @Test
  fun pushNewTaskReplace_whenCurrentTopIsNewState_detachesAllAndPushesToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_2,
      RouterNavigator.Flag.NEW_TASK_REPLACE,
      attachTransition3,
      detachTransition3
    )
    verify(detachTransition2).willDetachFromHost(router2, TestState.STATE_2, null, false)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2)
    Truth.assertThat(routerNavigator.size()).isEqualTo(1)
  }

  @Test
  fun pushReplaceTop_removeExistingTopOfStack_andShouldPushNewStateToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.REPLACE_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3)
    Truth.assertThat(routerNavigator.size()).isEqualTo(2)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    Truth.assertThat(routerNavigator.size()).isEqualTo(1)
  }

  @Test
  fun pushReplaceTop_whenStackIsEmpty_shouldPushNewStateToTopOfStack() {
    routerNavigator.pushState(
      TestState.STATE_1,
      RouterNavigator.Flag.REPLACE_TOP,
      attachTransition3,
      detachTransition3
    )
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    Truth.assertThat(routerNavigator.size()).isEqualTo(1)
  }

  @Test
  fun pop_whenThereIsSomethingToPopTo_shouldRemoveCurrentItemAndReaddPreviousItemWithCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, null)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.popState()
    verify(detachTransition2)
      .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_1, false)
    verify(hostRouter).detachChild(router2)
    verify(attachTransition1)
      .willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, false)
  }

  @Test
  fun pop_whenThereIsSomethingInTheStackAndATransientState_shouldRemoveTransientTate() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, null)
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2)
    routerNavigator.pushState(
      TestState.STATE_3,
      RouterNavigator.Flag.TRANSIENT,
      attachTransition3,
      detachTransition3
    )
    routerNavigator.popState()
    verify(detachTransition3)
      .willDetachFromHost(router3, TestState.STATE_3, TestState.STATE_2, false)
  }

  @Test
  fun pop_whenThereIsNothingToPopTo_shouldRemoveCurrentItem() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    routerNavigator.popState()
    verify(detachTransition1).willDetachFromHost(router1, TestState.STATE_1, null, false)
    verify(hostRouter).detachChild(router1)
  }

  @Test
  fun pop_whenTheRouterNavigatorIsEmpty_shouldNotCrash() {
    routerNavigator.popState()
  }

  @Test
  fun peekRouter() {
    Truth.assertThat(routerNavigator.peekRouter()).isNull()
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    Truth.assertThat(routerNavigator.peekRouter()).isEqualTo(router1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekRouter()).isNull()
  }

  @Test
  fun peekState() {
    Truth.assertThat(routerNavigator.peekState()).isNull()
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1)
    Truth.assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1)
    routerNavigator.popState()
    Truth.assertThat(routerNavigator.peekState()).isNull()
  }
}
