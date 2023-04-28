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
@file:Suppress("invisible_reference", "invisible_member")

package com.uber.rib.core

import android.app.Application
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.autoDispose
import io.reactivex.Completable
import io.reactivex.CompletableSource
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope

@ExperimentalCoroutinesApi
/** returns the [TestScope] override currently installed for testing. */
public val ScopeProvider.testScopeOverride: TestScope?
  // Due to custom friend path usage, reference to LazyCoroutineScope will stay red in IDE
  get() =
    synchronized(LazyCoroutineScope) {
      val testScope = LazyCoroutineScope[this]
      return if (testScope != null && testScope is TestScope) testScope else null
    }

/**
 * Overrides [ScopeProvider.coroutineScope] with a [TestScope] with lifecycle integration for
 * testing. Accessible directly as [TestScope] via [ScopeProvider.TestScopeOverride].
 */
@ExperimentalCoroutinesApi
public fun ScopeProvider.enableTestScopeOverride(
  context: CoroutineContext = SupervisorJob(),
): Unit = synchronized(LazyCoroutineScope) { LazyCoroutineScope[this] = asTestScope(context) }

/** Disables the [ScopeProvider.coroutineScope] override with [TestScope] */
public fun ScopeProvider.disableTestScopeOverride(): Unit =
  synchronized(LazyCoroutineScope) { LazyCoroutineScope[this] = null }

/** returns the [TestScope] override currently installed for testing. */
@ExperimentalCoroutinesApi
public val Application.testScopeOverride: TestScope?
  // Due to custom friend path usage, reference to LazyCoroutineScope will stay red in IDE
  get() =
    synchronized(LazyCoroutineScope) {
      val testScope = LazyCoroutineScope[this]
      return if (testScope != null && testScope is TestScope) testScope else null
    }

/**
 * Overrides [ScopeProvider.coroutineScope] with a [TestScope] with lifecycle integration for
 * testing. Accessible directly as [TestScope] via [ScopeProvider.TestScopeOverride].
 */
@ExperimentalCoroutinesApi
public fun Application.enableTestScopeOverride(context: CoroutineContext = SupervisorJob()): Unit =
  synchronized(LazyCoroutineScope) { LazyCoroutineScope[this] = TestScope(context) }

/** Disables the [ScopeProvider.coroutineScope] override with [TestScope] */
public fun Application.disableTestScopeOverride(): Unit =
  synchronized(LazyCoroutineScope) { LazyCoroutineScope[this] = null }

/** Returns a new [TestScope] from the [ScopeProvider] */
@ExperimentalCoroutinesApi
public fun ScopeProvider.asTestScope(context: CoroutineContext = SupervisorJob()): TestScope {
  return requestScope().asTestScope(context)
}

/** Returns a new [TestScope] from the [CompletableSource] */
@ExperimentalCoroutinesApi
public fun CompletableSource.asTestScope(context: CoroutineContext = SupervisorJob()): TestScope {
  val scope = TestScope(context)
  Completable.wrap(this).autoDispose(scope).subscribe({ scope.cancel() }) { e ->
    scope.cancel("OnError", e)
  }

  return scope
}
