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

package com.uber.rib.workflow.core;

import androidx.annotation.Nullable;

import com.uber.rib.core.Optional;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Java6Assertions.assertThat;

/** Utility to expose {@link Observable} instances on a {@link Step} for unit testing purposes. */
public final class StepTester {

  private StepTester() {}

  /**
   * Exposes a {@link Step} instances observable for testing purposes.
   *
   * @param step to expose observable for.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step.
   * @return a {@link Observable} that runs the steps action.
   */
  public static <T, A extends ActionableItem>
      Observable<Optional<Step.Data<T, A>>> exposeObservable(Step<T, A> step) {
    return step.asObservable();
  }

  /**
   * Exposes the {@link com.uber.rib.workflow.core.Step.Data} of a {@link Step}
   *
   * @param step to expose data for.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step.
   * @return the data of the step
   */
  @Nullable
  public static <T, A extends ActionableItem> T exposeStepData(Step.Data<T, A> step) {
    return step.getValue();
  }

  /**
   * Asserts that no {@link Step} has been emitted from the {@link TestObserver}
   *
   * @param testSubscriber the step subscriber to assert on.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step.
   */
  public static <T, A extends ActionableItem> void assertStepNotYetEmitted(
      TestObserver<Optional<Step.Data<T, A>>> testSubscriber) {
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Asserts that exactly one {@link Step} has been emitted from the {@link TestObserver}
   *
   * @param testSubscriber the step subscriber to assert on.
   * @param <T> type of return value for a step.
   * @param <A> type of next actionable item for a step.
   */
  public static <T, A extends ActionableItem> void assertStepEmitted(
      TestObserver<Optional<Step.Data<T, A>>> testSubscriber) {
    testSubscriber.assertValueCount(1);
    Optional<Step.Data<T, A>> stepData = testSubscriber.values().get(0);
    assertThat(stepData.isPresent()).isTrue();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }
}
