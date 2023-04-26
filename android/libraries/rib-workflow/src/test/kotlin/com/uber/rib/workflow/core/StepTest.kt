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
package com.uber.rib.workflow.core

import com.google.common.base.Optional
import com.google.common.truth.Truth.assertThat
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.workflow.core.Step.Data
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StepTest {

  @get:Rule var androidSchedulersRuleRx2 = AndroidSchedulersRule()

  private val interactorLifecycleSubject = BehaviorSubject.create<InteractorEvent>()
  private val returnValueSubject: PublishSubject<Optional<Data<Any, ActionableItem>>> =
    PublishSubject.create()
  private lateinit var step: Step<Any, ActionableItem>

  @Before
  fun setup() {
    step = Step.fromOptional(returnValueSubject.singleOrError())
  }

  @Test
  fun asObservable_withInactiveLifecycle_shouldWaitForActiveLifecycleBeforeEmitting() {
    val returnValue = Any()
    val testSubscriber: TestObserver<Optional<Data<Any, ActionableItem>>> =
      TestObserver<Optional<Data<Any, ActionableItem>>>()
    step.asObservable().subscribe(testSubscriber)
    testSubscriber.assertNoValues()
    testSubscriber.assertNoErrors()
    testSubscriber.assertNotComplete()
    returnValueSubject.onNext(
      Optional.of(Data(returnValue, ActionableItem { interactorLifecycleSubject.hide() })),
    )
    returnValueSubject.onComplete()
    testSubscriber.assertNoValues()
    testSubscriber.assertNoErrors()
    testSubscriber.assertNotComplete()
    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE)
    testSubscriber.assertValueCount(1)
    assertThat(testSubscriber.values()[0].get().getValue()).isEqualTo(returnValue)
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
  }

  @Test
  fun asObservable_withActiveLifecycle_shouldEmitWithoutWaiting() {
    val returnValue = Any()
    val testSubscriber: TestObserver<Optional<Data<Any, ActionableItem>>> =
      TestObserver<Optional<Data<Any, ActionableItem>>>()
    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE)
    step.asObservable().subscribe(testSubscriber)
    testSubscriber.assertNoValues()
    testSubscriber.assertNoErrors()
    testSubscriber.assertNotComplete()
    returnValueSubject.onNext(
      Optional.of(Data(returnValue, ActionableItem { interactorLifecycleSubject.hide() })),
    )
    returnValueSubject.onComplete()
    testSubscriber.assertValueCount(1)
    assertThat(testSubscriber.values()[0].get().getValue()).isEqualTo(returnValue)
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
  }

  @Test
  fun onStep_withASuccessFullFirstAction_shouldProperlyChainTheNextStep() {
    val returnValue = Any()
    val secondReturnValue = Any()
    val testSubscriber: TestObserver<Optional<Data<Any, ActionableItem>>> =
      TestObserver<Optional<Data<Any, ActionableItem>>>()
    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE)
    step
      .onStep { o, actionableItem ->
        Step.from(
          Observable.just(Data(secondReturnValue, actionableItem)).singleOrError(),
        )
      }
      .asObservable()
      .subscribe(testSubscriber)
    returnValueSubject.onNext(
      Optional.of(Data(returnValue, ActionableItem { interactorLifecycleSubject.hide() })),
    )
    returnValueSubject.onComplete()
    testSubscriber.assertValueCount(1)
    assertThat(testSubscriber.values()[0].get().getValue()).isEqualTo(secondReturnValue)
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
  }

  @Test
  fun onStep_withAnUnsuccessfulFirstAction_shouldTerminateTheWholeChain() {
    val testSubscriber: TestObserver<Optional<Data<Any, ActionableItem>>> =
      TestObserver<Optional<Data<Any, ActionableItem>>>()
    val secondReturnValue = Any()
    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE)
    step
      .onStep { _, actionableItem ->
        Step.from(
          Observable.just(Data(secondReturnValue, actionableItem)).singleOrError(),
        )
      }
      .asObservable()
      .subscribe(testSubscriber)
    returnValueSubject.onNext(Optional.absent())
    returnValueSubject.onComplete()
    testSubscriber.assertValueCount(1)
    assertThat(testSubscriber.values()[0].isPresent).isFalse()
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
  }
}
