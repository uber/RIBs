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
package com.uber.rib.workflow.core;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.Optional;
import com.uber.rib.core.lifecycle.InteractorEvent;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class StepTest {
  @Rule public final AndroidSchedulersRule androidSchedulersRuleRx2 = new AndroidSchedulersRule();

  private final BehaviorSubject<InteractorEvent> interactorLifecycleSubject =
      BehaviorSubject.create();
  private final PublishSubject<Optional<Step.Data<Object, ActionableItem>>> returnValueSubject =
      PublishSubject.create();

  private Step<Object, ActionableItem> step;

  @Before
  public void setup() {
    step = Step.fromOptional(returnValueSubject.singleOrError());
  }

  @Test
  public void asObservable_withInactiveLifecycle_shouldWaitForActiveLifecycleBeforeEmitting() {
    Object returnValue = new Object();
    TestObserver<Optional<Step.Data<Object, ActionableItem>>> testSubscriber = new TestObserver<>();

    step.asObservable().subscribe(testSubscriber);

    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();

    returnValueSubject.onNext(
        Optional.of(new Step.Data<>(returnValue, () -> interactorLifecycleSubject)));
    returnValueSubject.onComplete();

    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).get().getValue()).isEqualTo(returnValue);
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  @Test
  public void asObservable_withActiveLifecycle_shouldEmitWithoutWaiting() {
    Object returnValue = new Object();
    TestObserver<Optional<Step.Data<Object, ActionableItem>>> testSubscriber = new TestObserver<>();

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);

    step.asObservable().subscribe(testSubscriber);

    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();

    returnValueSubject.onNext(
        Optional.of(new Step.Data<>(returnValue, () -> interactorLifecycleSubject)));
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).get().getValue()).isEqualTo(returnValue);
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  @Test
  public void onStep_withASuccessFullFirstAction_shouldProperlyChainTheNextStep() {
    Object returnValue = new Object();
    Object secondReturnValue = new Object();
    TestObserver<Optional<Step.Data<Object, ActionableItem>>> testSubscriber = new TestObserver<>();

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);

    step.onStep(
            (o, actionableItem) ->
                Step.from(
                    Observable.just(new Step.Data<>(secondReturnValue, actionableItem))
                        .singleOrError()))
        .asObservable()
        .subscribe(testSubscriber);

    returnValueSubject.onNext(
        Optional.of(new Step.Data<>(returnValue, () -> interactorLifecycleSubject)));
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).get().getValue()).isEqualTo(secondReturnValue);
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  @Test
  public void onStep_withAnUnsuccessfulFirstAction_shouldTerminateTheWholeChain() {
    TestObserver<Optional<Step.Data<Object, ActionableItem>>> testSubscriber = new TestObserver<>();
    Object secondReturnValue = new Object();

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);

    step.onStep(
            (o, actionableItem) ->
                Step.from(
                    Observable.just(new Step.Data<>(secondReturnValue, actionableItem))
                        .singleOrError()))
        .asObservable()
        .subscribe(testSubscriber);

    returnValueSubject.onNext(Optional.absent());
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).isPresent()).isFalse();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }
}
