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
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.WeakHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty

/**
 * [CoroutineScope] tied to this [ScopeProvider].
 * This scope will be canceled when ScopeProvider is completed
 *
 * This scope is bound to
 * [RibDispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
 */
val ScopeProvider.coroutineScope: CoroutineScope by LazyCoroutineScope {
  var context: CoroutineContext = SupervisorJob() + RibDispatchers.Main.immediate + CoroutineName("${this::class.simpleName}:coroutineScope")

  RibCoroutinesConfig.exceptionHandler?.let {
    context += RibCoroutinesConfig.exceptionHandler!!
  }
  asCoroutineScope(context)
}

internal class LazyCoroutineScope(val initializer: ScopeProvider.() -> CoroutineScope) {
  companion object {
    val values = WeakHashMap<ScopeProvider, CoroutineScope>()
  }
  operator fun getValue(thisRef: ScopeProvider, property: KProperty<*>): CoroutineScope = synchronized(values) {
    return values.getOrPut(thisRef) { thisRef.initializer() }
  }
}
