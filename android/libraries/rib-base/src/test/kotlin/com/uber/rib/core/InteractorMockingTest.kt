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
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.TestRibLifecycle
import com.uber.rib.core.lifecycle.coroutineScope
import com.uber.rib.core.lifecycle.emit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class InteractorMockingTest {
  @get:Rule val rule = RibCoroutinesRule()
  private val lifecycleFlow = MutableSharedFlow<InteractorEvent>(1, 0, BufferOverflow.DROP_OLDEST)
  private val ribLifecycle = TestRibLifecycle(InteractorEvent.ACTIVE..InteractorEvent.INACTIVE)
  private val mock1 = mock<Interactor<*, *>> { on { this.lifecycleFlow } doReturn lifecycleFlow }
  private val mock2 = mock<Interactor<*, *>> { on { this.ribLifecycle } doReturn ribLifecycle }

  @Test
  fun testMock1() = runTest {
    lifecycleFlow.tryEmit(InteractorEvent.ACTIVE)
    test1(mock1) { lifecycleFlow.tryEmit(InteractorEvent.INACTIVE) }
  }

  @Test
  fun testMock2() = runTest {
    ribLifecycle.emit(InteractorEvent.ACTIVE)
    test1(mock2) { ribLifecycle.emit(InteractorEvent.INACTIVE) }
  }
}

private fun TestScope.test1(mock: Interactor<*, *>, cancel: () -> Unit) {
  var started = false
  var completed = false
  mock.coroutineScope.launch {
    try {
      started = true
      awaitCancellation()
    } finally {
      completed = true
    }
  }
  runCurrent()
  assertThat(started).isTrue()
  cancel()
  runCurrent()
  assertThat(completed).isTrue()
}
