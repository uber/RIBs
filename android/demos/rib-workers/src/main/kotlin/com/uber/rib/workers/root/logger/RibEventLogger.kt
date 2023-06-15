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
package com.uber.rib.workers.root.logger

import android.util.Log
import com.uber.rib.core.RibEventDurationData
import com.uber.rib.workers.root.logger.RibEventLogger.log

/**
 * Sample of consuming [ribEventDataFlow] possibilities.
 * 1. Can pipe Interactor/Router/Presenter/Worker information to backend
 * 2. Could report expensive workers on Ui thread and crash on Debug builds for early detection
 * 3. More tailored aggregation if needed.
 *
 * IMPORTANT: Given logic at [log] will be running upon Interactor/Router/Presenter/Worker
 * ATTACH/DETACH, the added logic should guaranteed that we are not impacting app performance
 */
object RibEventLogger {

  private const val LOG_TAG = "RibEventLogger"

  fun log(ribEventData: RibEventDurationData) {
    val message = ribEventData.buildWorkerDurationMessage()
    Log.d(LOG_TAG, message)
  }

  private fun RibEventDurationData.buildWorkerDurationMessage(): String {
    return "RibEventDurationData: ${this.ribComponentType}_${this.ribEventType} at ${this.className} took ${this.totalBindingDurationMilli} ms in thread: ${this.threadName}"
  }
}
