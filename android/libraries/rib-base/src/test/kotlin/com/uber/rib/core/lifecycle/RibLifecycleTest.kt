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
package com.uber.rib.core.lifecycle

import com.google.common.truth.Truth.assertThat
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleNotStartedException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RibLifecycleTest {
  private val lifecycle = TestRibLifecycle(LifecycleEvent.ACTIVE..LifecycleEvent.INACTIVE)

  @Test(expected = LifecycleNotStartedException::class)
  fun `when getting coroutineScope before lifecycle started, throws exception`() = runTest {
    lifecycle.coroutineScope
  }

  @Test(expected = LifecycleEndedException::class)
  fun `when getting coroutineScope after lifecycle ended, throws exception`() = runTest {
    lifecycle.active()
    lifecycle.inactive()
    lifecycle.coroutineScope
  }

  @Test
  fun `when lifecycle ends, coroutineScope is cancelled`() = runTest {
    lifecycle.active()
    val scope = lifecycle.coroutineScope
    lifecycle.inactive()
    assertThat(scope.coroutineContext.job.isCancelled).isTrue()
  }

  @Test
  fun `when getting coroutineScope multiple times, instances are the same`() = runTest {
    lifecycle.active()
    val set = List(50) { lifecycle.coroutineScope }.toSet()
    assertThat(set).hasSize(1)
    lifecycle.inactive()
  }

  @Test
  fun `verify CoroutineName`() = runTest {
    lifecycle.active()
    val name = lifecycle.coroutineScope.coroutineContext[CoroutineName]?.name
    assertThat(name).isEqualTo("TestRibLifecycle:coroutineScope")
    lifecycle.inactive()
  }
}

enum class LifecycleEvent {
  ACTIVE,
  INACTIVE,
}

private fun TestRibLifecycle<LifecycleEvent>.active() = emit(LifecycleEvent.ACTIVE)

private fun TestRibLifecycle<LifecycleEvent>.inactive() = emit(LifecycleEvent.INACTIVE)
