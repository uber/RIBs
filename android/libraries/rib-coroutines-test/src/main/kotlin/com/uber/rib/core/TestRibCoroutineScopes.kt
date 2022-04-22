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

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.autoDispose
import io.reactivex.Completable
import io.reactivex.CompletableSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
/**
 * returns the [TestCoroutineScope] override currently installed for testing.
 */
val ScopeProvider.testCoroutineScopeOverride: TestCoroutineScope?
  // Due to custom friend path usage, reference to LazyCoroutineScope will stay red in IDE
  get() = synchronized(LazyCoroutineScope.values) {
    val testScope = LazyCoroutineScope.values[this]
    return if (testScope != null && testScope is TestCoroutineScope) testScope else null
  }

/**
 * Overrides [ScopeProvider.coroutineScope] with a [TestCoroutineScope] with lifecycle integration for testing.
 * Accessible directly as [TestCoroutineScope] via [ScopeProvider.testCoroutineScopeOverride].
 */
@ExperimentalCoroutinesApi
fun ScopeProvider.enableTestCoroutineScopeOverride(context: CoroutineContext = SupervisorJob()) = synchronized(LazyCoroutineScope.values) {
  LazyCoroutineScope.values[this] = asTestCoroutineScope(context)
}

/**
 * Disables the [ScopeProvider.coroutineScope] override with [TestCoroutineScope]
 */
fun ScopeProvider.disableTestCoroutineScopeOverride() = synchronized(LazyCoroutineScope.values) {
  LazyCoroutineScope.values.remove(this)
}

/**
 * Returns a new [TestCoroutineScope] from the [ScopeProvider]
 */
@ExperimentalCoroutinesApi
fun ScopeProvider.asTestCoroutineScope(context: CoroutineContext = SupervisorJob()): TestCoroutineScope {
  return requestScope().asTestCoroutineScope(context)
}

/**
 * Returns a new [TestCoroutineScope] from the [CompletableSource]
 */
@ExperimentalCoroutinesApi
fun CompletableSource.asTestCoroutineScope(context: CoroutineContext = SupervisorJob()): TestCoroutineScope {
  val scope = TestCoroutineScope(context)
  Completable.wrap(this)
    .autoDispose(scope)
    .subscribe({ scope.cancel() }) { e -> scope.cancel("OnError", e) }

  return scope
}
