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
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleNotStartedException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class RibCoroutineScopesTest(private val failSilentlyOnLifecycleEnded: Boolean) {
  @get:Rule val ribCoroutinesRule = RibCoroutinesRule()
  private val interactor = TestInteractor()

  @Before
  fun setUp() {
    RibCoroutinesConfig.shouldCoroutineScopeFailSilentlyOnLifecycleEnded =
      failSilentlyOnLifecycleEnded
  }

  @Test
  fun coroutineScope_whenCalledBeforeActive_throws() {
    assertThrows(LifecycleNotStartedException::class.java) { interactor.coroutineScope }
  }

  @Test
  fun coroutineScope_whenCalledAfterInactive_throws() {
    interactor.attachAndDetach {}
    if (failSilentlyOnLifecycleEnded) {
      assertThat(interactor.coroutineScope.isActive).isFalse()
    } else {
      assertThrows(LifecycleEndedException::class.java) { interactor.coroutineScope }
    }
  }

  @Test
  fun coroutineScope_whenCalledWhileActive_cancelsWhenInactive() = runTest {
    var launched = false
    val job: Job
    interactor.attachAndDetach {
      job =
        coroutineScope.launch {
          launched = true
          awaitCancellation()
        }
      runCurrent()
      assertThat(launched).isTrue()
      assertThat(job.isActive).isTrue()
    }
    assertThat(job.isCancelled).isTrue()
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "failSilentlyOnLifecycleEnded = {0}")
    fun data() = listOf(arrayOf(true), arrayOf(false))
  }
}

private class TestInteractor : Interactor<Unit, Router<*>>()

@OptIn(ExperimentalContracts::class)
private inline fun TestInteractor.attachAndDetach(block: TestInteractor.() -> Unit) {
  contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
  InteractorHelper.attach(this, Unit, mock(), null)
  block()
  InteractorHelper.detach(this)
}
