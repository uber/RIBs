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
package com.uber.rib.core

import com.google.common.truth.Truth
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import org.junit.Test

class WorkerTest {

  @Test
  fun threadingType_withDefaultWorker_shouldHaveEmptyCoroutineContextAsDefault() {
    val defaultWorker = DefaultWorker()
    Truth.assertThat(defaultWorker.coroutineContext).isEqualTo(EmptyCoroutineContext)
  }

  @Test
  fun threadingType_withBackgroundWorker_shouldUseDefaultDispatchers() {
    val backgroundWorker = BackgroundWorker()
    Truth.assertThat(backgroundWorker.coroutineContext).isEqualTo(RibDispatchers.Default)
  }

  private class DefaultWorker : Worker

  private class BackgroundWorker : Worker {
    override val coroutineContext: CoroutineContext = RibDispatchers.Default
  }
}
