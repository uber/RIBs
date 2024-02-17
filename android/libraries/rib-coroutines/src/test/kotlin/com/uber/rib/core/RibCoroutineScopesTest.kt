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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class RibCoroutineScopesTest(
  private val throwWhenBeforeActive: Boolean,
  private val throwWhenAfterInactive: Boolean,
) {
  @get:Rule val ribCoroutinesRule = RibCoroutinesRule()

  @get:Rule val exceptionRule: ExpectedException = ExpectedException.none()

  private val interactor = TestInteractor()

  @Before
  fun setUp() {
    RibCoroutinesConfig.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
      when (throwable) {
        is CoroutineScopeLifecycleException -> if (shouldThrow(throwable)) throw throwable
        else -> throw throwable
      }
    }
  }

  @Test
  fun coroutineScope_whenCalledBeforeActive_throwsCoroutineScopeLifecycleException() = runTest {
    if (throwWhenBeforeActive) {
      exceptionRule.expect(CoroutineScopeLifecycleException::class.java)
      exceptionRule.expectCause(instanceOf(LifecycleNotStartedException::class.java))
    }
    assertThat(interactor.coroutineScope.isActive).isFalse()
  }

  @Test
  fun coroutineScope_whenCalledAfterInactive_throwsCoroutineScopeLifecycleException() = runTest {
    if (throwWhenAfterInactive) {
      exceptionRule.expect(CoroutineScopeLifecycleException::class.java)
      exceptionRule.expectCause(instanceOf(LifecycleEndedException::class.java))
    }
    interactor.attachAndDetach {}
    assertThat(interactor.coroutineScope.isActive).isFalse()
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

  private fun shouldThrow(e: CoroutineScopeLifecycleException): Boolean =
    (throwWhenBeforeActive && e.cause is LifecycleNotStartedException) ||
      (throwWhenAfterInactive && e.cause is LifecycleEndedException)

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "throwWhenBeforeActive = {0}, throwWhenAfterInactive = {1}")
    fun data() =
      listOf(
        arrayOf(true, true),
        arrayOf(true, false),
        arrayOf(false, true),
        arrayOf(false, false),
      )
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
