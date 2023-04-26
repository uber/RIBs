/*
 * Copyright (C) 2022. Uber Technologies
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

import android.app.Application
import com.google.common.truth.Truth.assertThat
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
internal class RibScopesTest {

  @get:Rule var rule = RibCoroutinesRule()

  @Test
  internal fun testScopeLifecycle() = runTest {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()
    val job =
      interactor.coroutineScope.launch {
        while (isActive) {
          delay(5L)
        }
      }
    assertThat(job.isActive).isTrue()
    interactor.detach()
    assertThat(job.isActive).isFalse()
  }

  @Test
  internal fun testScopeLifecycleWithTestScope() = runTest {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()
    interactor.enableTestScopeOverride()

    val job =
      interactor.coroutineScope.launch {
        while (isActive) {
          delay(5L)
        }
      }
    assertThat(job.isActive).isTrue()
    interactor.detach()
    assertThat(job.isActive).isFalse()
  }

  @Test()
  internal fun testScopeCaching() {
    val interactor1 = FakeInteractor<Presenter, Router<*>>()
    val interactor2 = FakeInteractor<Presenter, Router<*>>()
    interactor1.attach()
    interactor2.attach()

    val interactor1mainScope1 = interactor1.coroutineScope
    val interactor1mainScope2 = interactor1.coroutineScope
    val interactor2mainScope1 = interactor2.coroutineScope

    assertThat(interactor1mainScope1).isEqualTo(interactor1mainScope2)
    assertThat(interactor1mainScope1).isNotEqualTo(interactor2mainScope1)
  }

  // Bad test: The RuntimeException thrown is actually NoSuchElementException (handler exceptions is
  // empty).
  @Test(expected = RuntimeException::class)
  internal fun testUncaughtHandler() = runTest {
    val handler = TestUncaughtExceptionCaptor()
    RibCoroutinesConfig.exceptionHandler = handler

    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()
    interactor.coroutineScope.launch { throw RuntimeException("mainScope failed") }
    throw (handler.exceptions.first())
  }

  // Bad test: The RuntimeException is actually thrown by Interactor.requestScope(), because it is
  // called before
  // attaching the interactor.
  @Test(expected = RuntimeException::class)
  internal fun testException() = runTest {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.enableTestScopeOverride()
    interactor.attach()
    interactor.coroutineScope.launch { throw RuntimeException("mainScope failed") }
  }

  @Test()
  internal fun testSetTestScopeOverride() {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()

    assertThat(interactor.testScopeOverride).isNull()

    interactor.enableTestScopeOverride()
    val testScope = interactor.testScopeOverride
    val realScope = interactor.coroutineScope
    assertThat(testScope).isInstanceOf(TestScope::class.java)
    assertThat(testScope).isEqualTo(realScope)

    interactor.disableTestScopeOverride()
    val testScope2 = interactor.testScopeOverride
    val realScope2 = interactor.coroutineScope
    assertThat(testScope2).isNull()
    assertThat(realScope2).isNotInstanceOf(TestScope::class.java)
  }

  @Test()
  internal fun testSetTestScopeOnApplicationOverride() {
    // Can use mock since all logic is in extension function.
    val application: Application = mock()

    assertThat(application.testScopeOverride).isNull()

    application.enableTestScopeOverride()
    val testScope = application.testScopeOverride
    val realScope = application.coroutineScope
    assertThat(testScope).isInstanceOf(TestScope::class.java)
    assertThat(testScope).isEqualTo(realScope)

    application.disableTestScopeOverride()
    val testScope2 = application.testScopeOverride
    val realScope2 = application.coroutineScope
    assertThat(testScope2).isNull()
    assertThat(realScope2).isNotInstanceOf(TestScope::class.java)
  }

  @Test
  fun testScopeReattaching() {
    val interactor = object : BasicInteractor<Presenter, Router<*>>(mock()) {}
    with(interactor) {
      dispatchAttach(null)
      with(coroutineScope) {
        assertThat(isActive).isTrue()
        dispatchDetach()
        // after dispatching detach, we expect the same instance captured before to be inactive
        assertThat(isActive).isFalse()
      }
      dispatchAttach(null)
      // The previous instance of coroutineScope is permanently cancelled,
      // but ScopeProvider.coroutineScope should now return a new, active instance.
      assertThat(coroutineScope.isActive).isTrue()
    }
  }

  private class TestUncaughtExceptionCaptor : CoroutineExceptionHandler {
    var exceptions = mutableListOf<Throwable>()

    override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler
    override fun handleException(context: CoroutineContext, exception: Throwable) {
      exceptions.add(exception)
    }
  }
}
