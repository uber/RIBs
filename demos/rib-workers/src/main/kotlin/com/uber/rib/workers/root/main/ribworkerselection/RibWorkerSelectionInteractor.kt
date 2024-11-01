/*
 * Copyright (C) 2023. Uber Technologies
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
package com.uber.rib.workers.root.main.ribworkerselection

import android.util.Log
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.RibEvents
import com.uber.rib.core.WorkerBinder
import com.uber.rib.core.asRibCoroutineWorker
import com.uber.rib.core.asWorker
import com.uber.rib.core.bind
import com.uber.rib.core.coroutineScope
import com.uber.rib.workers.root.main.workers.BackgroundWorker
import com.uber.rib.workers.root.main.workers.DefaultRibCoroutineWorker
import com.uber.rib.workers.root.main.workers.DefaultWorker
import com.uber.rib.workers.root.main.workers.IoWorker
import com.uber.rib.workers.root.main.workers.UiWorker
import com.uber.rib.workers.util.EventStream
import com.uber.rib.workers.util.StateStream
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RibWorkerSelectionInteractor(
  presenter: ComposePresenter,
  private val eventStream: EventStream<RibWorkerBindTypeClickType>,
  private val stateStream: StateStream<RibWorkerSelectionViewModel>,
  private val backgroundWorker: BackgroundWorker,
  private val defaultWorker: DefaultWorker,
  private val ioWorker: IoWorker,
  private val uiWorker: UiWorker,
  private val defaultRibCoroutineWorker: DefaultRibCoroutineWorker,
) : BasicInteractor<ComposePresenter, RibWorkerSelectionRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)

    Log.d(this.javaClass.name, "WorkerLogger enabled? ${RibEvents.areRibActionEmissionsAllowed}")

    eventStream
      .observe()
      .onEach {
        when (it) {
          RibWorkerBindTypeClickType.SINGLE_WORKER_BIND_CALLER_THREAD -> {
            updateViewModel(uiWorker::class.simpleName)
            WorkerBinder.bind(this, uiWorker)
          }
          RibWorkerBindTypeClickType.SINGLE_WORKER_BIND_BACKGROUND_THREAD -> {
            updateViewModel(ioWorker::class.simpleName)
            WorkerBinder.bind(this, ioWorker)
          }
          RibWorkerBindTypeClickType.BIND_MULTIPLE_DEPRECATED_WORKERS -> {
            val workers = listOf(backgroundWorker, defaultWorker, ioWorker, uiWorker)
            updateViewModel("Multiple deprecated workers ")
            WorkerBinder.bind(this, workers)
          }
          RibWorkerBindTypeClickType.BIND_MULTIPLE_RIB_COROUTINE_WORKERS -> {
            val workers = listOf(defaultRibCoroutineWorker, defaultRibCoroutineWorker)
            updateViewModel("Multiple RibCoroutineWorkers ")
            coroutineScope.bind(workers)
          }
          RibWorkerBindTypeClickType.BIND_RIB_COROUTINE_WORKER -> {
            updateViewModel(defaultRibCoroutineWorker::class.simpleName)
            coroutineScope.bind(defaultRibCoroutineWorker)
          }
          RibWorkerBindTypeClickType.WORKER_TO_RIB_COROUTINE_WORKER -> {
            val ribCoroutineWorker = uiWorker.asRibCoroutineWorker()
            updateViewModel(ribCoroutineWorker::class.simpleName)
            coroutineScope.bind(ribCoroutineWorker)
          }
          RibWorkerBindTypeClickType.RIB_COROUTINE_WORKER_TO_WORKER -> {
            val worker = defaultRibCoroutineWorker.asWorker()
            updateViewModel(worker::class.simpleName)
            WorkerBinder.bind(this, worker)
          }
        }
      }
      .launchIn(coroutineScope)
  }

  private fun updateViewModel(workerBound: String?) {
    with(stateStream) {
      dispatch(
        current()
          .copy(
            workerInfo = workerBound + WORKER_BOUND_MESSAGE,
          ),
      )
    }
  }

  private companion object {
    const val WORKER_BOUND_MESSAGE =
      " being bound. Please check Logcat with [WorkerBinderInfo] tag for full binding info"
  }
}
