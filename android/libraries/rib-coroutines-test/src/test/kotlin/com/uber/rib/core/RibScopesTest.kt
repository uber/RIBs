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
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException

@OptIn(ExperimentalCoroutinesApi::class)
internal class RibScopesTest {

  @get:Rule var rule = RibCoroutinesRule()

  @Test
  internal fun testScopeLifecycle() = runBlockingTest {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()
    val job = interactor.coroutineScope.launch {
      while (isActive) {
        delay(5L)
      }
    }
    assertThat(job.isActive).isTrue()
    interactor.detach()
    assertThat(job.isActive).isFalse()
  }

  @Test
  internal fun testScopeLifecycleWithTestScope() = runBlockingTest {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()
    interactor.enableTestCoroutineScopeOverride()

    val job = interactor.coroutineScope.launch {
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

  @Test(expected = RuntimeException::class)
  internal fun testUncaughtHandler() = runBlockingTest {
    val handler = TestCoroutineExceptionHandler()
    RibCoroutinesConfig.exceptionHandler = handler

    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()
    interactor.coroutineScope.launch {
      throw RuntimeException("mainScope failed")
    }
    handler.cleanupTestCoroutines()
  }

  @Test(expected = RuntimeException::class)
  internal fun testException() = runBlockingTest {

    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.enableTestCoroutineScopeOverride()
    interactor.attach()
    interactor.coroutineScope.launch {
      throw RuntimeException("mainScope failed")
    }
    interactor.testCoroutineScopeOverride!!.cleanupTestCoroutines()
  }

  @Test()
  internal fun testSetTestScopeOverride() {

    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()

    assertThat(interactor.testCoroutineScopeOverride).isNull()

    interactor.enableTestCoroutineScopeOverride()
    val testScope = interactor.testCoroutineScopeOverride
    val realScope = interactor.coroutineScope
    assertThat(testScope).isInstanceOf(TestCoroutineScope::class.java)
    assertThat(testScope).isEqualTo(realScope)

    interactor.disableTestCoroutineScopeOverride()
    val testScope2 = interactor.testCoroutineScopeOverride
    val realScope2 = interactor.coroutineScope
    assertThat(testScope2).isNull()
    assertThat(realScope2).isNotInstanceOf(TestCoroutineScope::class.java)
  }

  @Test()
  internal fun testSetTestScopeOnApplicationOverride() {

    // Can use mock since all logic is in extension function.
    val application: Application = mock()

    assertThat(application.testCoroutineScopeOverride).isNull()

    application.enableTestCoroutineScopeOverride()
    val testScope = application.testCoroutineScopeOverride
    val realScope = application.coroutineScope
    assertThat(testScope).isInstanceOf(TestCoroutineScope::class.java)
    assertThat(testScope).isEqualTo(realScope)

    application.disableTestCoroutineScopeOverride()
    val testScope2 = application.testCoroutineScopeOverride
    val realScope2 = application.coroutineScope
    assertThat(testScope2).isNull()
    assertThat(realScope2).isNotInstanceOf(TestCoroutineScope::class.java)
  }
}
