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

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable

/**
 * A dispatcher that immediately executes the [Runnable] on the same stack frame, without
 * potentially forming event loops like [Unconfined][kotlinx.coroutines.Dispatchers.Unconfined] or
 * [Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate] in case of nested
 * coroutines.
 *
 * For more context, see the following issues on `kotlinx.coroutines` GitHub repository:
 * <!-- spotless:off -->
 * 1. [Immediate dispatchers can cause spooky action at a distance](https://github.com/Kotlin/kotlinx.coroutines/issues/3760)
 * 2. [Chaining rxSingle calls that use Unconfined dispatcher and blockingGet results in deadlock #3458](https://github.com/Kotlin/kotlinx.coroutines/issues/3458)
 * 3. [Coroutines/Flow vs add/remove listener (synchronous execution) #3506](https://github.com/Kotlin/kotlinx.coroutines/issues/3506)
 * <!-- spotless:on -->
 */
internal object DirectDispatcher : CoroutineDispatcher() {
  override fun dispatch(context: CoroutineContext, block: Runnable) {
    block.run()
  }
}
