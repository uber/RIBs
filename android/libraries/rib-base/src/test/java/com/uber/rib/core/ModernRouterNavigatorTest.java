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

import com.uber.rib.core.RouterNavigator.AttachTransition;
import com.uber.rib.core.RouterNavigator.DetachTransition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ModernRouterNavigatorTest {

  private enum TestState implements RouterNavigatorState {
    STATE_1,
    STATE_2,
    STATE_3
  }

  @Mock private AttachTransition<Router, TestState> attachTransitionOne;
  @Mock private DetachTransition<Router, TestState> detachTransitionOne;
  @Mock private AttachTransition<Router, TestState> attachTransitionTwo;
  @Mock private DetachTransition<Router, TestState> detachTransitionTwo;
  @Mock private AttachTransition<Router, TestState> attachTransitionThree;
  @Mock private DetachTransition<Router, TestState> detachTransitionThree;
  @Mock private Router hostRouter;
  @Mock private Router routerOne;
  @Mock private Router routerTwo;
  @Mock private Router routerThree;

  private RouterNavigator<TestState> routerNavigator;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    when(attachTransitionOne.buildRouter()).thenReturn(routerOne);
    when(attachTransitionTwo.buildRouter()).thenReturn(routerTwo);
    when(attachTransitionThree.buildRouter()).thenReturn(routerThree);

    routerNavigator = new ModernRouterNavigator<>(hostRouter);
  }

  @Test
  public void hostWillDetach_whenThereIsAnAttachedRouter_shouldRunDetachRunner() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    routerNavigator.hostWillDetach();

    verify(detachTransitionOne).willDetachFromHost(routerOne, TestState.STATE_1, null, false);
  }

  @Test
  public void hostWillDetach_whenThereIsAnAttachedRouter_andAttachesAgain_shouldReattach() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    routerNavigator.hostWillDetach();

    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    verify(attachTransitionOne, times(2))
        .willAttachToHost(routerOne, null, TestState.STATE_1, true);
  }

  @Test
  public void
      pushRetained_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    verify(attachTransitionOne).willAttachToHost(routerOne, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(routerOne);
    verifyZeroInteractions(detachTransitionOne);

    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);

    verify(detachTransitionOne)
        .willDetachFromHost(routerOne, TestState.STATE_1, TestState.STATE_2, true);
    verify(hostRouter).detachChild(routerOne);
    verify(attachTransitionTwo)
        .willAttachToHost(routerTwo, TestState.STATE_1, TestState.STATE_2, true);
    verifyZeroInteractions(detachTransitionTwo);
  }

  @Test
  public void
      pushTransient_whenNotInitialPush_shouldRunPreviousDetachRunnerAndRunNewAttachRunnerWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    verify(attachTransitionOne).willAttachToHost(routerOne, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(routerOne);
    verifyZeroInteractions(detachTransitionOne);

    routerNavigator.pushTransientState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);

    verify(detachTransitionOne)
        .willDetachFromHost(routerOne, TestState.STATE_1, TestState.STATE_2, true);
    verify(hostRouter).detachChild(routerOne);
    verify(attachTransitionTwo)
        .willAttachToHost(routerTwo, TestState.STATE_1, TestState.STATE_2, true);
    verifyZeroInteractions(detachTransitionTwo);
  }

  @Test
  public void pushRetained_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    verify(attachTransitionOne).willAttachToHost(routerOne, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(routerOne);
    verifyZeroInteractions(detachTransitionOne);

    Answer attachChildAnswer =
        new Answer() {
          @Override
          public Void answer(InvocationOnMock invocation) throws Throwable {
            routerNavigator.pushRetainedState(
                TestState.STATE_3, attachTransitionThree, detachTransitionThree);
            return null;
          }
        };
    doAnswer(attachChildAnswer).when(hostRouter).attachChild(routerTwo);

    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);

    verify(detachTransitionOne)
        .willDetachFromHost(routerOne, TestState.STATE_1, TestState.STATE_2, true);
    verify(attachTransitionTwo)
        .willAttachToHost(routerTwo, TestState.STATE_1, TestState.STATE_2, true);
    verify(detachTransitionTwo)
        .willDetachFromHost(routerTwo, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransitionThree)
        .willAttachToHost(routerThree, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransitionThree, never())
        .willAttachToHost(routerThree, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushTransient_whenChildRouterSwitchesStateImmediately_shouldSwitchToCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    verify(attachTransitionOne).willAttachToHost(routerOne, null, TestState.STATE_1, true);
    verify(hostRouter).attachChild(routerOne);
    verifyZeroInteractions(detachTransitionOne);

    Answer attachChildAnswer =
        new Answer() {
          @Override
          public Void answer(InvocationOnMock invocation) throws Throwable {
            routerNavigator.pushRetainedState(
                TestState.STATE_3, attachTransitionThree, detachTransitionThree);
            return null;
          }
        };
    doAnswer(attachChildAnswer).when(hostRouter).attachChild(routerTwo);

    routerNavigator.pushTransientState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);

    verify(detachTransitionOne)
        .willDetachFromHost(routerOne, TestState.STATE_1, TestState.STATE_2, true);
    verify(attachTransitionTwo)
        .willAttachToHost(routerTwo, TestState.STATE_1, TestState.STATE_2, true);
    verify(detachTransitionTwo)
        .willDetachFromHost(routerTwo, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransitionThree)
        .willAttachToHost(routerThree, TestState.STATE_2, TestState.STATE_3, true);
    verify(attachTransitionThree, never())
        .willAttachToHost(routerThree, TestState.STATE_1, TestState.STATE_3, true);
  }

  @Test
  public void pushRetained_overACurrentTransientRouter_shouldPopTheTransientRouter() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    verify(attachTransitionOne).willAttachToHost(routerOne, null, TestState.STATE_1, true);

    routerNavigator.pushTransientState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);

    verify(detachTransitionOne)
        .willDetachFromHost(routerOne, TestState.STATE_1, TestState.STATE_2, true);

    routerNavigator.pushRetainedState(
        TestState.STATE_3, attachTransitionThree, detachTransitionThree);

    verify(detachTransitionTwo)
        .willDetachFromHost(routerTwo, TestState.STATE_2, TestState.STATE_3, true);

    routerNavigator.popState();

    verify(detachTransitionThree)
        .willDetachFromHost(routerThree, TestState.STATE_3, TestState.STATE_1, false);
    verify(attachTransitionOne)
        .willAttachToHost(routerOne, TestState.STATE_3, TestState.STATE_1, false);
  }

  @Test
  public void
      pop_whenThereIsSomethingToPopTo_shouldRemoveCurrentItemAndReaddPreviousItemWithCorrectState() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne);
    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);

    routerNavigator.popState();

    verify(detachTransitionTwo)
        .willDetachFromHost(routerTwo, TestState.STATE_2, TestState.STATE_1, false);
    verify(hostRouter).detachChild(routerTwo);
    verify(attachTransitionOne)
        .willAttachToHost(routerOne, TestState.STATE_2, TestState.STATE_1, false);
  }

  @Test
  public void pop_whenThereIsSomethingInTheStackAndATransientState_shouldRemoveTransientTate() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne);
    routerNavigator.pushRetainedState(TestState.STATE_2, attachTransitionTwo, detachTransitionTwo);
    routerNavigator.pushTransientState(
        TestState.STATE_3, attachTransitionThree, detachTransitionThree);

    routerNavigator.popState();

    verify(detachTransitionThree)
        .willDetachFromHost(routerThree, TestState.STATE_3, TestState.STATE_2, false);
  }

  @Test
  public void pop_whenThereIsNothingToPopTo_shouldRemoveCurrentItem() {
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);

    routerNavigator.popState();

    verify(detachTransitionOne).willDetachFromHost(routerOne, TestState.STATE_1, null, false);
    verify(hostRouter).detachChild(routerOne);
  }

  @Test
  public void pop_whenTheRouterNavigatorIsEmpty_shouldNotCrash() {
    routerNavigator.popState();
  }

  @Test
  public void peekRouter() {
    assertThat(routerNavigator.peekRouter()).isNull();
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);
    assertThat(routerNavigator.peekRouter()).isEqualTo(routerOne);
    routerNavigator.popState();
    assertThat(routerNavigator.peekRouter()).isNull();
  }

  @Test
  public void peekState() {
    assertThat(routerNavigator.peekState()).isNull();
    routerNavigator.pushRetainedState(TestState.STATE_1, attachTransitionOne, detachTransitionOne);
    assertThat(routerNavigator.peekState()).isEqualTo(TestState.STATE_1);
    routerNavigator.popState();
    assertThat(routerNavigator.peekState()).isNull();
  }
}
