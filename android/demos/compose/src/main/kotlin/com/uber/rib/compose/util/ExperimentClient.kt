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
import java.lang.Math.random

class ExperimentClientImpl(private val application: Application) : ExperimentClient {
  override fun isTreated(id: String): Boolean {
    val result = random() > 0.5
    val message = "isTreated($id) = $result"
    Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT).show()
    Log.d(this::class.java.simpleName, message)
    return result
  }
}

object NoOpExperimentClient : ExperimentClient {
  override fun isTreated(id: String) = false
}

interface ExperimentClient {
  fun isTreated(id: String): Boolean
}
