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
package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.rib.core.WorkerBinder.bind
import com.uber.rib.core.WorkerBinder.bindToWorkerLifecycle
import com.uber.rib.core.WorkerBinder.mapInteractorLifecycleToWorker
import com.uber.rib.core.WorkerBinder.mapPresenterLifecycleToWorker
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.PresenterEvent
import com.uber.rib.core.lifecycle.WorkerEvent
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class WorkerBinderTest(private val adaptFromRibCoroutineWorker: Boolean) {
  @get:Rule val ribCoroutinesRule = RibCoroutinesRule()

  private val worker =
    mock<Worker>().run {
      if (adaptFromRibCoroutineWorker) {
        spy(this.asRibCoroutineWorker().asWorker())
      } else {
        this
      }
    }
  private val workerBinderListener: WorkerBinderListener = mock()

  private val fakeWorker = FakeWorker()
  private val interactor = FakeInteractor<Presenter, Router<*>>()

  @Before
  fun setUp() {
    WorkerBinder.initializeMonitoring(workerBinderListener)
  }

  @Test
  fun bind_whenInteractorAttached_shouldStartWorker() {
    val lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE)
    bind(mapInteractorLifecycleToWorker(lifecycle), worker)
    verify(worker).onStart(any())
  }

  @Test
  fun bind_whenInteractorDetached_shouldStopWorker() {
    val lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE)
    bind(mapInteractorLifecycleToWorker(lifecycle), worker)
    lifecycle.accept(InteractorEvent.INACTIVE)
    verify(worker).onStop()
  }

  @Test
  fun unbind_whenInteractorAttached_shouldStopWorker() {
    val lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE)
    val unbinder = bind(mapInteractorLifecycleToWorker(lifecycle), worker)
    unbinder.unbind()
    verify(worker).onStop()
  }

  @Test
  fun unbind_whenOutsideInteractorLifecycle_shouldNotCallStopAgain() {
    val lifecycle = BehaviorRelay.createDefault(InteractorEvent.INACTIVE)
    val unbinder = bind(mapInteractorLifecycleToWorker(lifecycle), worker)
    verify(worker, times(1)).onStop()
    unbinder.unbind()
    verify(worker, times(1)).onStop()
  }

  @Test
  fun onInactive_whenAfterUnbind_shouldNotCallStopAgain() {
    val lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE)
    val unbinder = bind(mapInteractorLifecycleToWorker(lifecycle), worker)
    unbinder.unbind()
    verify(worker, times(1)).onStop()
    lifecycle.accept(InteractorEvent.INACTIVE)
    verify(worker, times(1)).onStop()
  }

  @Test
  fun bindToWorkerLifecycle_whenStartEventEmitted_shouldStartWorker() {
    val lifecycle = BehaviorRelay.createDefault(WorkerEvent.START)
    bindToWorkerLifecycle(lifecycle, worker)
    verify(worker).onStart(any())
  }

  @Test
  fun bindToWorkerLifecycle_whenStopEventEmitted_shouldStopWorker() {
    val lifecycle = BehaviorRelay.createDefault(WorkerEvent.START)
    bindToWorkerLifecycle(lifecycle, worker)
    lifecycle.accept(WorkerEvent.STOP)
    verify(worker).onStop()
  }

  @Test
  fun bind_whenPresenterAttached_shouldStartWorker() {
    val lifecycle = BehaviorRelay.createDefault(PresenterEvent.LOADED)
    bind(mapPresenterLifecycleToWorker(lifecycle), worker)
    verify(worker).onStart(any())
  }

  @Test
  fun bind_whenPresenterDetached_shouldStopWorker() {
    val lifecycle = BehaviorRelay.createDefault(PresenterEvent.LOADED)
    bind(mapPresenterLifecycleToWorker(lifecycle), worker)
    lifecycle.accept(PresenterEvent.UNLOADED)
    verify(worker).onStop()
  }

  @Test
  fun bind_onStartIsCalledEagerly() {
    val interactor = object : Interactor<Any, Router<*>>() {}
    var onStartCalled = false
    val worker = Worker { onStartCalled = true }
    InteractorHelper.attach(interactor, Unit, mock(), null)
    bind(interactor, worker)
    assertThat(onStartCalled).isTrue()
  }

  @Test
  fun bind_whenSubscribeToLifecycleInWorker_observerIsCalledEagerly() {
    val interactor = object : Interactor<Any, Router<*>>() {}
    var enteredUnconfined = false
    val worker = Worker {
      val subscription = interactor.lifecycle().subscribe { enteredUnconfined = true }
      assertThat(enteredUnconfined).isTrue()
      subscription.dispose()
    }
    InteractorHelper.attach(interactor, Unit, mock(), null)
    bind(interactor, worker)
    assertThat(enteredUnconfined).isTrue()
  }

  @Test
  fun unbind_whenPresenterAttached_shouldStopWorker() {
    val lifecycle = BehaviorRelay.createDefault(PresenterEvent.LOADED)
    val unbinder = bind(mapPresenterLifecycleToWorker(lifecycle), worker)
    unbinder.unbind()
    verify(worker).onStop()
  }

  @Test
  fun unbind_whenOutsidePresenterLifecycle_shouldNotCallStopAgain() {
    val lifecycle = BehaviorRelay.createDefault(PresenterEvent.UNLOADED)
    val unbinder = bind(mapPresenterLifecycleToWorker(lifecycle), worker)
    verify(worker, times(1)).onStop()
    unbinder.unbind()
    verify(worker, times(1)).onStop()
  }

  @Test
  fun bind_withUnconfinedCoroutineDispatchers_shouldReportBinderInformationForOnStart() = runTest {
    val binderDurationCaptor = argumentCaptor<WorkerBinderInfo>()
    bindFakeWorker()
    verify(workerBinderListener).onBindCompleted(binderDurationCaptor.capture())
    binderDurationCaptor.firstValue.assertWorkerDuration(
      "FakeWorker",
      WorkerEvent.START,
      RibDispatchers.Unconfined,
    )
  }

  @Test
  fun bind_multipleWorkers_shouldReportBinderTwice() = runTest {
    val uiWorker = UiWorker()
    val binderDurationCaptor = argumentCaptor<WorkerBinderInfo>()
    prepareInteractor()
    val workers = listOf(fakeWorker, fakeWorker, uiWorker)
    bind(interactor, workers)
    advanceUntilIdle()
    verify(workerBinderListener, times(3)).onBindCompleted(binderDurationCaptor.capture())
    binderDurationCaptor.firstValue.assertWorkerDuration(
      "FakeWorker",
      WorkerEvent.START,
      RibDispatchers.Unconfined,
    )
    binderDurationCaptor.thirdValue.assertWorkerDuration(
      "UiWorker",
      WorkerEvent.START,
      RibDispatchers.Main,
    )
  }

  @Test
  fun unbind_withUnconfinedCoroutineDispatchers_shouldReportBinderDurationForOnStop() = runTest {
    val binderDurationCaptor = argumentCaptor<WorkerBinderInfo>()
    val unbinder = bindFakeWorker()
    unbinder.unbind()
    verify(workerBinderListener, times(2)).onBindCompleted(binderDurationCaptor.capture())
    binderDurationCaptor.secondValue.assertWorkerDuration(
      "FakeWorker",
      WorkerEvent.STOP,
      RibDispatchers.Unconfined,
    )
  }

  private fun bindFakeWorker(): WorkerUnbinder {
    prepareInteractor()
    return bind(interactor, fakeWorker)
  }

  private fun prepareInteractor() {
    interactor.attach()
    interactor.enableTestScopeOverride()
  }

  private fun WorkerBinderInfo.assertWorkerDuration(
    expectedWorkerClassName: String,
    expectedWorkerEvent: WorkerEvent,
    expectedCoroutineContext: CoroutineContext,
  ) {
    assertThat(workerName).contains(expectedWorkerClassName)
    assertThat(workerEvent).isEqualTo(expectedWorkerEvent)
    assertThat(expectedCoroutineContext).isEqualTo(expectedCoroutineContext)
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "adaptFromRibCoroutineWorker = {0}")
    fun data() = listOf(arrayOf(true), arrayOf(false))
  }
}

private fun Worker(onStartBlock: (WorkerScopeProvider) -> Unit) =
  object : Worker {
    override fun onStart(lifecycle: WorkerScopeProvider) {
      onStartBlock(lifecycle)
    }
  }

class UiWorker : Worker {
  override val coroutineContext: CoroutineDispatcher = RibDispatchers.Main
}
