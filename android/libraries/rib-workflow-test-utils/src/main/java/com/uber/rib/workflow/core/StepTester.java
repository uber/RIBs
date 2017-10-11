package com.uber.rib.workflow.core;

import android.support.annotation.Nullable;

import com.ubercab.common.base.Optional;

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
