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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.uber.rib.core.RouterNavigator.AttachTransition;
import com.uber.rib.core.RouterNavigator.DetachTransition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

public class StackRouterNavigatorTest {

  private enum TestState implements RouterNavigatorState {
    STATE_1,
    STATE_2,
    STATE_3
  }

  @Mock private AttachTransition<Router, TestState> attachTransition1;
  @Mock private DetachTransition<Router, TestState> detachTransition1;
  @Mock private AttachTransition<Router, TestState> attachTransition2;
  @Mock private DetachTransition<Router, TestState> detachTransition2;
  @Mock private AttachTransition<Router, TestState> attachTransition3;
  @Mock private DetachTransition<Router, TestState> detachTransition3;
  @Mock private Router hostRouter;
  @Mock private Router router1;
  @Mock private Router router2;
  @Mock private Router router3;

  private StackRouterNavigator<TestState> routerNavigator;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    when(attachTransition1.buildRouter()).thenReturn(router1);
    when(attachTransition2.buildRouter()).thenReturn(router2);
    when(attachTransition3.buildRouter()).thenReturn(router3);

    routerNavigator = new StackRouterNavigator<>(hostRouter);
  }

  @Test
  public void hostWillDetach_whenThereIsAnAttachedRouter_shouldRunDetachRunner() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    routerNavigator.hostWillDetach();

    verify(detachTransition1).willDetachFromHost(router1, TestState.STATE_1, null, false);
  }

  @Test
  public void hostWillDetach_whenThereIsAnAttachedRouter_andAttachesAgain_shouldReattach() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    routerNavigator.hostWillDetach();

    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1, times(2)).willAttachToHost(router1, null, TestState.STATE_1, true);
  }

  @Test
  public void
      pushRetained_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(hostRouter).detachChild(router1);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verifyZeroInteractions(detachTransition2);
  }

  @Test
  public void
      pushTransientDeprecated_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    routerNavigator.pushTransientState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(hostRouter).detachChild(router1);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verifyZeroInteractions(detachTransition2);
  }

  @Test
  public void pushRetained_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    Answer attachChildAnswer =
        invocation -> {
          routerNavigator.pushRetainedState(
              TestState.STATE_3, attachTransition3, detachTransition3);
          return null;
        };
    doAnswer(attachChildAnswer).when(hostRouter).attachChild(router2);

    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void
      pushTransientDeprecated_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    Answer attachChildAnswer =
        invocation -> {
          routerNavigator.pushRetainedState(
              TestState.STATE_3, attachTransition3, detachTransition3);
          return null;
        };
    doAnswer(attachChildAnswer).when(hostRouter).attachChild(router2);

    routerNavigator.pushTransientState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushRetained_overACurrentTransientRouter_shouldPopTheTransientRouter() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);

    routerNavigator.pushTransientState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);

    routerNavigator.pushRetainedState(TestState.STATE_3, attachTransition3, detachTransition3);

    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true);

    routerNavigator.popState();

    verify(detachTransition3)
        .willDetachFromHost(router3, TestState.STATE_3, TestState.STATE_1, false);
    verify(attachTransition1)
        .willAttachToHost(router1, TestState.STATE_3, TestState.STATE_1, false);
  }

  @Test
  public void detachAll_whenThereIsAnAttachedRouter_shouldRunDetachRunner() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    routerNavigator.detachAll();

    verify(detachTransition1).willDetachFromHost(router1, TestState.STATE_1, null, false);
  }

  @Test
  public void detachAll_whenThereIsAnAttachedRouter_andAttachesAgain_shouldReattach() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    routerNavigator.detachAll();

    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1, times(2)).willAttachToHost(router1, null, TestState.STATE_1, true);
  }

  @Test
  public void
      pushDefault_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(hostRouter).detachChild(router1);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verifyZeroInteractions(detachTransition2);
  }

  @Test
  public void
      pushTransient_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    routerNavigator.pushState(
        TestState.STATE_2,
        StackRouterNavigator.Flag.TRANSIENT,
        attachTransition2,
        detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(hostRouter).detachChild(router1);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verifyZeroInteractions(detachTransition2);
  }

  @Test
  public void pushDefault_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    Answer attachChildAnswer =
        invocation -> {
          routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3);
          return null;
        };
    doAnswer(attachChildAnswer).when(hostRouter).attachChild(router2);

    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushTransient_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(router1);
    verifyZeroInteractions(detachTransition1);

    Answer attachChildAnswer =
        invocation -> {
          routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3);
          return null;
        };
    doAnswer(attachChildAnswer).when(hostRouter).attachChild(router2);

    routerNavigator.pushState(
        TestState.STATE_2,
        StackRouterNavigator.Flag.TRANSIENT,
        attachTransition2,
        detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);
    verify(attachTransition2).willAttachToHost(router2, TestState.STATE_1, TestState.STATE_2, true);
    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushDefault_overACurrentTransientRouter_shouldPopTheTransientRouter() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    verify(attachTransition1).willAttachToHost(router1, null, TestState.STATE_1, true);

    routerNavigator.pushState(
        TestState.STATE_2,
        StackRouterNavigator.Flag.TRANSIENT,
        attachTransition2,
        detachTransition2);

    verify(detachTransition1)
        .willDetachFromHost(router1, TestState.STATE_1, TestState.STATE_2, true);

    routerNavigator.pushState(TestState.STATE_3, attachTransition3, detachTransition3);

    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_3, true);

    routerNavigator.popState();

    verify(detachTransition3)
        .willDetachFromHost(router3, TestState.STATE_3, TestState.STATE_1, false);
    verify(attachTransition1)
        .willAttachToHost(router1, TestState.STATE_3, TestState.STATE_1, false);
  }

  @Test
  public void pushClearTop_whenNotInStack_shouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_3,
        StackRouterNavigator.Flag.CLEAR_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }

  @Test
  public void pushClearTop_whenInStack_shouldRemoveStatesToThatState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_1,
        StackRouterNavigator.Flag.CLEAR_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushClearTop_whenTopOfStack_shouldNotReorderOrAttach() {
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(
        TestState.STATE_1,
        StackRouterNavigator.Flag.CLEAR_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushSingleTop_whenNotInStack_shouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_3,
        StackRouterNavigator.Flag.SINGLE_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }

  @Test
  public void pushSingleTop_whenInStack_shouldPushStateToTopAndRemoveOtherInstances() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_1,
        StackRouterNavigator.Flag.SINGLE_TOP,
        attachTransition3,
        detachTransition3);

    verify(attachTransition3).willAttachToHost(router3, TestState.STATE_2, TestState.STATE_1, true);
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }

  @Test
  public void pushSingleTop_whenTopOfStack_shouldNotReorderStack() {
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(
        TestState.STATE_1,
        StackRouterNavigator.Flag.SINGLE_TOP,
        attachTransition3,
        detachTransition3);

    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_2, TestState.STATE_1, true);
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }

  @Test
  public void pushReorderTop_whenNotInStack_shouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_3,
        StackRouterNavigator.Flag.REORDER_TO_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }

  @Test
  public void pushReorderTop_whenInStack_shouldRemoveStatesToThatState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_1,
        StackRouterNavigator.Flag.REORDER_TO_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushReorderTop_whenTopOfStackStack_shouldNotReorderStack() {
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(
        TestState.STATE_1,
        StackRouterNavigator.Flag.REORDER_TO_TOP,
        attachTransition3,
        detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
    verify(attachTransition1).willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, true);
    verify(attachTransition3, never())
        .willAttachToHost(router3, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushNewTask_detachesCurrentAndClearsCurrentStack_andShouldPushToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);

    routerNavigator.pushState(
        TestState.STATE_3,
        StackRouterNavigator.Flag.NEW_TASK,
        attachTransition3,
        detachTransition3);

    verify(detachTransition2).willDetachFromHost(router2, TestState.STATE_2, null, false);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3);
    assertThat(routerNavigator.size()).isEqualTo(1);
  }

  @Test
  public void pushNewTask_whenCurrentTopIsNewState_onlyClearsTheBackStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);

    routerNavigator.pushState(
        TestState.STATE_2,
        StackRouterNavigator.Flag.NEW_TASK,
        attachTransition3,
        detachTransition3);

    verifyZeroInteractions(detachTransition2);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_2);
    assertThat(routerNavigator.size()).isEqualTo(1);
  }

  @Test
  public void pushReplaceTop_removeExistingTopOfStack_andShouldPushNewStateToTopOfStack() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);

    routerNavigator.pushState(
            TestState.STATE_3,
            StackRouterNavigator.Flag.REPLACE_TOP,
            attachTransition3,
            detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_3);
    assertThat(routerNavigator.size()).isEqualTo(2);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    assertThat(routerNavigator.size()).isEqualTo(1);
  }

  @Test
  public void pushReplaceTop_whenStackIsEmpty_shouldPushNewStateToTopOfStack() {
    routerNavigator.pushState(
            TestState.STATE_1,
            StackRouterNavigator.Flag.REPLACE_TOP,
            attachTransition3,
            detachTransition3);

    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    assertThat(routerNavigator.size()).isEqualTo(1);
  }

  @Test
  public void
      pop_whenThereIsSomethingToPopTo_shouldRemoveCurrentItemAndReaddPreviousItemWithCorrectState() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, null);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);

    routerNavigator.popState();

    verify(detachTransition2)
        .willDetachFromHost(router2, TestState.STATE_2, TestState.STATE_1, false);
    verify(hostRouter).detachChild(router2);
    verify(attachTransition1)
        .willAttachToHost(router1, TestState.STATE_2, TestState.STATE_1, false);
  }

  @Test
  public void pop_whenThereIsSomethingInTheStackAndATransientState_shouldRemoveTransientTate() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, null);
    routerNavigator.pushState(TestState.STATE_2, attachTransition2, detachTransition2);
    routerNavigator.pushState(
        TestState.STATE_3,
        StackRouterNavigator.Flag.TRANSIENT,
        attachTransition3,
        detachTransition3);

    routerNavigator.popState();

    verify(detachTransition3)
        .willDetachFromHost(router3, TestState.STATE_3, TestState.STATE_2, false);
  }

  @Test
  public void pop_whenThereIsNothingToPopTo_shouldRemoveCurrentItem() {
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);

    routerNavigator.popState();

    verify(detachTransition1).willDetachFromHost(router1, TestState.STATE_1, null, false);
    verify(hostRouter).detachChild(router1);
  }

  @Test
  public void pop_whenTheRouterNavigatorIsEmpty_shouldNotCrash() {
    routerNavigator.popState();
  }

  @Test
  public void peekRouter() {
    assertThat(routerNavigator.peekRouter()).isNull();
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    assertThat(routerNavigator.peekRouter()).isEqualTo(router1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekRouter()).isNull();
  }

  @Test
  public void peekState() {
    assertThat(routerNavigator.peekState()).isNull();
    routerNavigator.pushState(TestState.STATE_1, attachTransition1, detachTransition1);
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }
}
