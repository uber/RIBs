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
import static io.reactivex.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler;

import androidx.annotation.NonNull;
import com.google.common.base.Optional;
import com.uber.rib.core.lifecycle.InteractorEvent;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class WorkflowTest {
  @Rule public final AndroidSchedulersRule androidSchedulersRuleRx2 = new AndroidSchedulersRule();

  private final BehaviorSubject<InteractorEvent> interactorLifecycleSubject =
      BehaviorSubject.create();
  private final PublishSubject<Step.Data<Object, ActionableItem>> returnValueSubject =
      PublishSubject.create();

  @Before
  public void setup() {
    setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
  }

  @After
  public void cleanup() {
    RxAndroidPlugins.reset();
  }

  @Test
  public void createSingle_shouldReturnASingleThatRunsTheWorkflow() {
    ActionableItem actionableItem = () -> interactorLifecycleSubject;

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
