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
@file:Suppress("invisible_reference", "invisible_member")

package com.uber.rib.workflow.core

import com.google.common.base.Optional
import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import io.reactivex.observers.TestObserver

/** Utility to expose [Observable] instances on a [Step] for unit testing purposes. */
object StepTester {
  /**
   * Exposes a [Step] instances observable for testing purposes.
   *
   * @param step to expose observable for.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step.
   * @return a [Observable] that runs the steps action. </A></T>
   */
  @JvmStatic
  fun <T, A : ActionableItem> exposeObservable(
    step: Step<T, A>,
  ): Observable<Optional<Step.Data<T, A>>> {
    return step.asObservable()
  }

  /**
   * Exposes the [com.uber.rib.workflow.core.Step.Data] of a [Step]
   *
   * @param step to expose data for.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step.
   * @return the data of the step </A></T>
   */
  @JvmStatic
  fun <T, A : ActionableItem> exposeStepData(step: Step.Data<T, A>): T? {
    return step.getValue()
  }

  /**
   * Asserts that no [Step] has been emitted from the [TestObserver]
   *
   * @param testSubscriber the step subscriber to assert on.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step. </A></T>
   */
  @JvmStatic
  fun <T, A : ActionableItem> assertStepNotYetEmitted(
    testSubscriber: TestObserver<Optional<Step.Data<T, A>>>,
  ) {
    testSubscriber.run {
      assertNoValues()
      assertNotComplete()
      assertNoErrors()
    }
  }

  /**
   * Asserts that exactly one [Step] has been emitted from the [TestObserver]
   *
   * @param testSubscriber the step subscriber to assert on.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step. </A></T>
   */
  @JvmStatic
  fun <T, A : ActionableItem> assertStepEmitted(
    testSubscriber: TestObserver<Optional<Step.Data<T, A>>>,
  ) {
    testSubscriber.assertValueCount(1)
    val stepData: Optional<Step.Data<T, A>> = testSubscriber.values()[0]
    assertThat(stepData.isPresent).isTrue()
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
  }
}
