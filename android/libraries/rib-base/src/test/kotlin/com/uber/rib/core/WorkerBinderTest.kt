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

import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.rib.core.WorkerBinder.bind
import com.uber.rib.core.WorkerBinder.bindToWorkerLifecycle
import com.uber.rib.core.WorkerBinder.mapInteractorLifecycleToWorker
import com.uber.rib.core.WorkerBinder.mapPresenterLifecycleToWorker
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.PresenterEvent
import com.uber.rib.core.lifecycle.WorkerEvent
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class WorkerBinderTest {
  private val worker: Worker = mock()

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
}
