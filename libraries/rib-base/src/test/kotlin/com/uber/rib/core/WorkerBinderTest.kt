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
import com.uber.rib.core.RibEvents.ribActionEvents
import com.uber.rib.core.RibEventsUtils.assertRibActionInfo
import com.uber.rib.core.WorkerBinder.bind
import com.uber.rib.core.WorkerBinder.bindToWorkerLifecycle
import com.uber.rib.core.WorkerBinder.mapInteractorLifecycleToWorker
import com.uber.rib.core.WorkerBinder.mapPresenterLifecycleToWorker
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.PresenterEvent
import com.uber.rib.core.lifecycle.WorkerEvent
import io.reactivex.observers.TestObserver
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

  private val fakeWorker = FakeWorker()
  private val interactor = FakeInteractor<Presenter, Router<*>>()
  private val ribActionInfoObserver = TestObserver<RibActionInfo>()

  @Before
  fun setUp() {
    RibEvents.enableRibActionEmissions()
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
  fun bind_onStartIsCalledEagerly() = runTest {
    val interactor = object : Interactor<Any, Router<*>>() {}
    var onStartCalled = false
    val worker = Worker { onStartCalled = true }
    InteractorHelper.attach(interactor, Unit, mock(), null)
    bind(interactor, worker)
    assertThat(onStartCalled).isTrue()
  }

  @Test
  fun bind_whenSubscribeToLifecycleInWorker_observerIsCalledEagerly() = runTest {
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
    ribActionEvents.subscribe(ribActionInfoObserver)
    bindFakeWorker()
    val ribActionInfoValues = ribActionInfoObserver.values()
    ribActionInfoValues
      .last()
      .assertRibActionInfo(
        RibEventType.ATTACHED,
        RibActionEmitterType.DEPRECATED_WORKER,
        RibActionState.COMPLETED,
        ribClassName = "com.uber.rib.core.FakeWorker",
      )
  }

  @Test
  fun bind_withDisabledRibActionEvents_shouldNotEmitActionEvents() = runTest {
    RibEvents.areRibActionEmissionsAllowed = false
    ribActionEvents.subscribe(ribActionInfoObserver)
    bindFakeWorker()
    assertThat(ribActionInfoObserver.values()).isEmpty()
  }

  @Test
  fun bind_multipleWorkers_shouldReportBinderUiWorker() = runTest {
    ribActionEvents.subscribe(ribActionInfoObserver)
    val uiWorker = UiWorker()
    prepareInteractor()
    val workers = listOf(fakeWorker, fakeWorker, uiWorker)
    bind(interactor, workers)
    advanceUntilIdle()
    val ribActionInfoValues = ribActionInfoObserver.values()
    ribActionInfoValues
      .last()
      .assertRibActionInfo(
        RibEventType.ATTACHED,
        RibActionEmitterType.DEPRECATED_WORKER,
        RibActionState.COMPLETED,
        ribClassName = "com.uber.rib.core.UiWorker",
      )
  }

  @Test
  fun unbind_withUnconfinedCoroutineDispatchers_shouldReportBinderDurationForOnStop() = runTest {
    ribActionEvents.subscribe(ribActionInfoObserver)
    val unbinder = bindFakeWorker()
    unbinder.unbind()
    val ribActionInfoValues = ribActionInfoObserver.values()
    ribActionInfoValues
      .last()
      .assertRibActionInfo(
        RibEventType.DETACHED,
        RibActionEmitterType.DEPRECATED_WORKER,
        RibActionState.COMPLETED,
        ribClassName = "com.uber.rib.core.FakeWorker",
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
