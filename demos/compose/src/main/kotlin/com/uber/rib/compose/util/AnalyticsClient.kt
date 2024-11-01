/*
 * Copyright (C) 2021. Uber Technologies
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
package com.uber.rib.compose.util

import android.app.Application
import android.util.Log
import android.widget.Toast

class AnalyticsClientImpl(private val application: Application) : AnalyticsClient {
  override fun trackClick(id: String) {
    track(id, EventType.CLICK)
  }

  override fun trackImpression(id: String) {
    track(id, EventType.IMPRESSION)
  }

  private fun track(id: String, type: EventType) {
    val message = "$type for $id @ ${System.currentTimeMillis()}"
    Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT).show()
    Log.d(this::class.java.simpleName, message)
  }

  enum class EventType {
    CLICK,
    IMPRESSION,
  }
}

object NoOpAnalyticsClient : AnalyticsClient {
  override fun trackClick(id: String) = Unit
  override fun trackImpression(id: String) = Unit
}

interface AnalyticsClient {
  fun trackClick(id: String)
  fun trackImpression(id: String)
}
