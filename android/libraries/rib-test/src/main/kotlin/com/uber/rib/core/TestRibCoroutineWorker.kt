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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent

/**
 * Binds [worker], runs [testBody] with the worker, and unbinds worker after [testBody] returns.
 *
 * This function calls [runCurrent] on the [TestScope] immediately after binding the [worker]. This
 * means that if, and only if, there's no delay in [RibCoroutineWorker.onStart] function, worker
 * will already be bound at the start of [testBody] lambda. If there are delays, calling
 * [advanceTimeBy] or [advanceUntilIdle] at the start of [testBody] is needed to complete the
 * binding.
 *
 * The same rationale applies for coroutines launched in the [CoroutineScope] parameter of
 * [RibCoroutineWorker.onStart]: if there are no delays involved, coroutines will be run until idle
 * or completed, otherwise, the aforementioned time advancing API must be used.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public inline fun <T : RibCoroutineWorker> TestScope.test(
  worker: T,
  testBody: TestScope.(T) -> Unit,
) {
  val dispatcher = StandardTestDispatcher(testScheduler)
  val handle = bind(worker, dispatcher)
  runCurrent()
  try {
    testBody(worker)
  } finally {
    handle.unbind()
    runCurrent()
  }
}
