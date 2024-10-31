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
package com.uber.rib.workers.root.main.workers

import android.app.Application
import android.widget.Toast
import com.uber.rib.core.RibDispatchers
import com.uber.rib.core.Worker
import com.uber.rib.core.WorkerScopeProvider
import kotlin.coroutines.CoroutineContext

/**
 * This worker is guaranteed to have onStart/onStop to always run on Main thread (independently of
 * any other default Dispatchers specified via WorkerBinder.bind)
 */
class UiWorker(private val activityContext: Application) : Worker {

  override val coroutineContext: CoroutineContext = RibDispatchers.Main

  override fun onStart(lifecycle: WorkerScopeProvider) {
    Toast.makeText(
        activityContext,
        "Ui Worker (onStart) call",
        Toast.LENGTH_LONG,
      )
      .show()
  }
}
