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
import com.uber.rib.core.RibActionEmitterType
import com.uber.rib.core.RibActionInfo
import com.uber.rib.core.RibActionState
import com.uber.rib.core.RibEvents
import java.lang.System.currentTimeMillis
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow

/**
 * Sample of consuming [ribActionEvents] possibilities.
 * 1. Can pipe Interactor/Router/Presenter/Worker information to backend
 * 2. Could report expensive workers on Ui thread and crash on Debug builds for early detection
 * 3. More tailored aggregation if needed.
 *
 * IMPORTANT: Given logic at [logWorkerDuration] will be running upon
 * Interactor/Router/Presenter/Worker ATTACH/DETACH, the added logic should guaranteed that we are
 * not impacting app performance
 */
object ApplicationLevelWorkerLogger {
  private const val LOG_TAG = "WorkerLogger"

  private val workerTimeStampMap = ConcurrentHashMap<String, Long>()

  @OptIn(DelicateCoroutinesApi::class)
  fun start() {
    RibEvents.enableRibActionEmissions()

    GlobalScope.launch {
      RibEvents.ribActionEvents
        .filter { it.ribActionEmitterType == RibActionEmitterType.DEPRECATED_WORKER }
        .asFlow()
        .collect { it.logWorkerDuration() }
    }
  }

  private fun RibActionInfo.logWorkerDuration() {
    if (ribActionState == RibActionState.STARTED && !ribActionEmitterName.isClassNameInMap()) {
      workerTimeStampMap[this.ribActionEmitterName] = currentTimeMillis()
    } else if (
      ribActionState == RibActionState.COMPLETED && ribActionEmitterName.isClassNameInMap()
    ) {
      val startedTimeStamp = workerTimeStampMap[ribActionEmitterName]
      startedTimeStamp?.let {
        val totalDuration = getTotalDuration(it)
        this.logDuration(totalDuration)
      }
      workerTimeStampMap.remove(ribActionEmitterName)
    }
  }

  private fun String.isClassNameInMap(): Boolean = workerTimeStampMap.containsKey(this)

  private fun getTotalDuration(preOnStartDuration: Long): Long =
    currentTimeMillis() - preOnStartDuration

  private fun RibActionInfo.logDuration(totalDuration: Long) {
    Log.d(
      LOG_TAG,
      "${this.ribActionEmitterName} ${this.ribEventType} took $totalDuration ms on $originalCallerThreadName thread",
    )
  }
}
