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
import com.uber.rib.core.RibActionInfo
import com.uber.rib.core.RibActionType
import com.uber.rib.core.RibComponentType
import com.uber.rib.core.RibDispatchers
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
 * IMPORTANT: Given logic at [log] will be running upon Interactor/Router/Presenter/Worker
 * ATTACH/DETACH, the added logic should guaranteed that we are not impacting app performance
 */
object ApplicationLevelWorkerLogger {
  private const val LOG_TAG = "RibEventLogger"

  private val concurrentHashMap: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

  @OptIn(DelicateCoroutinesApi::class)
  fun start() {
    GlobalScope.launch(RibDispatchers.Unconfined) {
      RibEvents.ribActionEvents
        .filter { it.ribComponentType == RibComponentType.DEPRECATED_WORKER }
        .asFlow()
        .collect { it.logWorkerDuration() }
    }
  }

  private fun RibActionInfo.logWorkerDuration() {
    val ribComponentKey = this.className
    if (ribActionType == RibActionType.STARTED && !ribComponentKey.isClassNameInMap()) {
      concurrentHashMap[ribComponentKey] = currentTimeMillis()
    }

    if (ribActionType == RibActionType.COMPLETED && ribComponentKey.isClassNameInMap()) {
      val preOnStartDuration = concurrentHashMap[ribComponentKey]
      preOnStartDuration?.let { this.logDuration(it) }
      concurrentHashMap.remove(ribComponentKey)
    }
  }

  private fun String.isClassNameInMap(): Boolean = concurrentHashMap.containsKey(this)

  private fun RibActionInfo.logDuration(preOnStartDuration: Long) {
    val totalDuration = currentTimeMillis() - preOnStartDuration
    val currentThreadName = Thread.currentThread().name
    Log.d(
      LOG_TAG,
      "WORKER_BINDING_INFO -> ${this.className} ${this.ribEventType} took $totalDuration ms on $currentThreadName thread",
    )
  }
}
