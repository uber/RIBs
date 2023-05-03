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
package com.uber.rib.workers.root.main.workers.monitoring

import android.util.Log
import com.uber.rib.core.WorkerBinderInfo
import com.uber.rib.core.WorkerBinderListener
import com.uber.rib.workers.BuildConfig
import java.lang.Exception

/**
 * Sample of a custom [WorkerBinderListener] possibilities.
 * 1. Could pipe Worker Binder information to backend
 * 2. Could report expensive workers on Ui thread and crash on Debug builds for early detection
 * 3. More tailored aggregation if needed.
 *
 * IMPORTANT: Given logic of [onBindCompleted] will be running upon each Worker onStart/onStop.
 * Added logic should guaranteed that we are not impacting app performance by having expensive logic
 * within its implementation.
 */
class RibWorkerMonitor(private val backendReporter: BackendReporter) : WorkerBinderListener {

  override fun onBindCompleted(workerBinderInfo: WorkerBinderInfo) {
    val message = workerBinderInfo.buildWorkerDurationMessage()
    Log.d(LOG_TAG, message)

    backendReporter.report(message)

    if (BuildConfig.DEBUG && workerBinderInfo.isExpensiveUiWorker()) {
      throw ExpensiveUiWorkerException(message)
    }
  }

  private fun WorkerBinderInfo.isExpensiveUiWorker(): Boolean {
    return this.threadName.contains(MAIN_THREAD_IDENTIFIER) &&
      this.totalBindingDurationMilli > MAIN_THRESHOLD_MILLI
  }

  private fun WorkerBinderInfo.buildWorkerDurationMessage(): String {
    return "WorkerBinderInfo: ${this.workerName} ${this.workerEvent} took ${this.totalBindingDurationMilli} ms. [${this.coroutineContext}] - [Thread: ${this.threadName}] [Total threads: ${Thread.activeCount()}]"
  }

  companion object {
    private const val LOG_TAG = "RibWorkerMonitor"
    private const val MAIN_THREAD_IDENTIFIER = "main"
    private const val MAIN_THRESHOLD_MILLI = 16
  }
}

class ExpensiveUiWorkerException(message: String) : Exception(message)
