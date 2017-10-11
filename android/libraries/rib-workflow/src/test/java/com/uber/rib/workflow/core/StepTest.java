package com.uber.rib.workflow.core;

import android.support.annotation.NonNull;

import com.uber.presidio.test.PresidioRobolectricTestBase;
import com.uber.rib.core.lifecycle.InteractorEvent;
import com.ubercab.common.base.Optional;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class StepTest extends PresidioRobolectricTestBase {

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
        Optional.of(
            new Step.Data<Object, ActionableItem>(
                returnValue,
                new ActionableItem() {
                  @NonNull
                  @Override
                  public Observable<InteractorEvent> lifecycle() {
                    return interactorLifecycleSubject;
                  }
                })));
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
        Optional.of(
            new Step.Data<Object, ActionableItem>(
                returnValue,
                new ActionableItem() {
                  @NonNull
                  @Override
                  public Observable<InteractorEvent> lifecycle() {
                    return interactorLifecycleSubject;
                  }
                })));
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).get().getValue()).isEqualTo(returnValue);
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  @Test
  public void onStep_withASuccessFullFirstAction_shouldProperlyChainTheNextStep() {
    Object returnValue = new Object();
    final Object secondReturnValue = new Object();
    TestObserver<Optional<Step.Data<Object, ActionableItem>>> testSubscriber = new TestObserver<>();

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);

    step.onStep(
            new BiFunction<Object, ActionableItem, Step<Object, ActionableItem>>() {
              @Override
              public Step<Object, ActionableItem> apply(Object o, ActionableItem actionableItem) {
                return Step.from(
                    Observable.just(new Step.Data<>(secondReturnValue, actionableItem))
                        .singleOrError());
              }
            })
        .asObservable()
        .subscribe(testSubscriber);

    returnValueSubject.onNext(
        Optional.of(
            new Step.Data<Object, ActionableItem>(
                returnValue,
                new ActionableItem() {
                  @NonNull
                  @Override
                  public Observable<InteractorEvent> lifecycle() {
                    return interactorLifecycleSubject;
                  }
                })));
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).get().getValue()).isEqualTo(secondReturnValue);
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  @Test
  public void onStep_withAnUnsuccessfulFirstAction_shouldTerminateTheWholeChain() {
    TestObserver<Optional<Step.Data<Object, ActionableItem>>> testSubscriber = new TestObserver<>();
    final Object secondReturnValue = new Object();

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE);

    step.onStep(
            new BiFunction<Object, ActionableItem, Step<Object, ActionableItem>>() {
              @Override
              public Step<Object, ActionableItem> apply(Object o, ActionableItem actionableItem) {
                return Step.from(
                    Observable.just(new Step.Data<>(secondReturnValue, actionableItem))
                        .singleOrError());
              }
            })
        .asObservable()
        .subscribe(testSubscriber);

    returnValueSubject.onNext(Optional.<Step.Data<Object, ActionableItem>>absent());
    returnValueSubject.onComplete();

    testSubscriber.assertValueCount(1);
    assertThat(testSubscriber.values().get(0).isPresent()).isFalse();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }
}
