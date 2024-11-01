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

import com.uber.rib.core.Worker
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.asCoroutineDispatcher

/** A Rib Worker to be guaranteed on a background thread with a limited pool size of 1 */
class BackgroundWorker() : Worker {

  override val coroutineContext: CoroutineContext =
    Executors.newFixedThreadPool(1).asCoroutineDispatcher()
}
