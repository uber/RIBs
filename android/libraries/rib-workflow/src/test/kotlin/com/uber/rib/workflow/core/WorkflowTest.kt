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
import com.uber.rib.workflow.core.Step.Companion.from
import com.uber.rib.workflow.core.Step.Data
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WorkflowTest {
  @get:Rule var androidSchedulersRuleRx2 = AndroidSchedulersRule()

  private val interactorLifecycleSubject = BehaviorSubject.create<InteractorEvent>()
  private val returnValueSubject: PublishSubject<Data<Any, ActionableItem>> =
    PublishSubject.create()

  @Before
  fun setup() {
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
  }

  @After
  fun cleanup() {
    RxAndroidPlugins.reset()
  }

  @Test
  fun createSingle_shouldReturnASingleThatRunsTheWorkflow() {
    val actionableItem = ActionableItem { interactorLifecycleSubject }

    val workflow: Workflow<Any, ActionableItem> =
      object : Workflow<Any, ActionableItem>() {
        override fun getSteps(rootActionableItem: ActionableItem): Step<Any, ActionableItem> {
          return from(returnValueSubject.singleOrError())
        }
      }

    val testSubscriber = TestObserver<Optional<Any>>()
    workflow.createSingle(actionableItem).subscribe(testSubscriber)

    interactorLifecycleSubject.onNext(InteractorEvent.ACTIVE)
    val returnValue = Any()
    returnValueSubject.onNext(Data(returnValue, actionableItem))
    returnValueSubject.onComplete()

    testSubscriber.assertValueCount(1)
    assertThat(testSubscriber.values()[0].get()).isEqualTo(returnValue)
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
  }
}
