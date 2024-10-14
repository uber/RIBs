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
package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
@Ignore(
  """
    Test only passes when running in isolation: RibEvents flows might've been accessed
    when running full suite.
  """
)
class RibEventsTest {
  private val extraBufferCapacity = 16

  @Before
  fun setUp() {
    RibEvents.setExtraBufferCapacity(extraBufferCapacity)
  }

  @After
  fun tearDown() {
    RibEvents.setExtraBufferCapacity(Channel.UNLIMITED)
  }

  @Test
  fun setExtraBufferCapacityTest() = runTest {
    val results = mutableListOf<RibRouterEvent>()
    backgroundScope.launch { RibEvents.routerEventsFlow.collect(results::add) }
    runCurrent()
    repeat(32) { RibEvents.emitRouterEvent(RibEventType.ATTACHED, mock(), mock()) }
    runCurrent()
    assertThat(results.size).isEqualTo(16)
  }
}
