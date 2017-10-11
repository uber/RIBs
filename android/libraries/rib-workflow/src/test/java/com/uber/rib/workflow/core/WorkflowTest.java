package com.uber.rib.workflow.core;

import android.support.annotation.NonNull;

import com.uber.presidio.test.PresidioRobolectricTestBase;
import com.uber.rib.core.lifecycle.InteractorEvent;
import com.ubercab.common.base.Optional;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class WorkflowTest extends PresidioRobolectricTestBase {

  private final BehaviorSubject<InteractorEvent> interactorLifecycleSubject =
      BehaviorSubject.create();
  private final PublishSubject<Step.Data<Object, ActionableItem>> returnValueSubject =
      PublishSubject.create();

  @Test
  public void createSingle_shouldReturnASingleThatRunsTheWorkflow() {
    ActionableItem actionableItem =
        new ActionableItem() {
          @NonNull
          @Override
          public Observable<InteractorEvent> lifecycle() {
            return interactorLifecycleSubject;
          }
        };

    Workflow<Object, ActionableItem> workflow =
        new Workflow<Object, ActionableItem>() {
          @NonNull
          @Override
          protected Step<Object, ActionableItem> getSteps(@NonNull ActionableItem actionableItem) {
            return Step.from(returnValueSubject.singleOrError());
          }
        };

    TestObserver<Optional<Object>> testSubscriber = new TestObserver<>();
    workflow.createSingle(actionableItem).subscribe(testSubscriber);

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);
    Object returnValue = new Object();
    returnValueSubject.onNext(new Step.Data<>(returnValue, actionableItem));
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).get()).isEqualTo(returnValue);
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }
}
