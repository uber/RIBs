/*
 * Copyright (C) 2024. Uber Technologies
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
package com.uber.rib.core.internal

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable

/**
 * A coroutine dispatcher that is not confined to any specific thread. It executes the initial
 * continuation of a coroutine in the current call-frame and lets the coroutine resume in whatever
 * thread that is used by the corresponding suspending function, without mandating any specific
 * threading policy.
 *
 * This dispatcher is similar to [Unconfined][com.uber.rib.core.RibDispatchers.Unconfined], with the
 * difference that it does not form an event-loop on nested coroutines, which implies that it has
 * predictable ordering of events with the tradeoff of a risk StackOverflowError.
 *
 * **This is internal API, not supposed to be used by library consumers.**
 */
@CoroutinesFriendModuleApi
public object DirectDispatcher : CoroutineDispatcher() {
  override fun dispatch(context: CoroutineContext, block: Runnable) {
    block.run()
  }
}
