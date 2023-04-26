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
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import java.util.WeakHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job

/**
 * [CoroutineScope] tied to this [ScopeProvider]. This scope will be canceled when ScopeProvider is
 * completed
 *
 * This scope is bound to
 * [RibDispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
 */
public val ScopeProvider.coroutineScope: CoroutineScope by
  LazyCoroutineScope<ScopeProvider> {
    val context: CoroutineContext =
      SupervisorJob() +
        RibDispatchers.Main.immediate +
        CoroutineName("${this::class.simpleName}:coroutineScope") +
        (RibCoroutinesConfig.exceptionHandler ?: EmptyCoroutineContext)

    asCoroutineScope(context)
  }

/**
 * [CoroutineScope] tied to this [Application]. This scope will not be cancelled, it lives for the
 * full application process.
 *
 * This scope is bound to
 * [RibDispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
 */
public val Application.coroutineScope: CoroutineScope by
  LazyCoroutineScope<Application> {
    val context: CoroutineContext =
      SupervisorJob() +
        RibDispatchers.Main.immediate +
        CoroutineName("${this::class.simpleName}:coroutineScope") +
        (RibCoroutinesConfig.exceptionHandler ?: EmptyCoroutineContext)

    CoroutineScope(context)
  }

internal class LazyCoroutineScope<This : Any>(val initializer: This.() -> CoroutineScope) {
  companion object {
    private val values = WeakHashMap<Any, CoroutineScope>()

    // Used to get and set Test overrides from rib-coroutines-test utils
    operator fun get(provider: Any) = values[provider]
    operator fun set(provider: Any, scope: CoroutineScope?) {
      values[provider] = scope
    }
  }
  operator fun getValue(thisRef: This, property: KProperty<*>): CoroutineScope =
    synchronized(LazyCoroutineScope) {
      return values.getOrPut(thisRef) {
        thisRef.initializer().apply {
          coroutineContext.job.invokeOnCompletion {
            synchronized(LazyCoroutineScope) { values.remove(thisRef) }
          }
        }
      }
    }
}
