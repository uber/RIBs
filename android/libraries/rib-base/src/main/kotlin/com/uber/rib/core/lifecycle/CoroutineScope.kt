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

import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleNotStartedException
import com.uber.rib.core.InternalRibsApi
import com.uber.rib.core.RibCoroutinesConfig
import com.uber.rib.core.RibDispatchers
import com.uber.rib.core.lifecycle.internal.CloseableOwner
import com.uber.rib.core.lifecycle.internal.ensureAlive
import com.uber.rib.core.lifecycle.internal.getCloseableOrPut
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

private const val SCOPE_REF_KEY = "com.uber.rib.core.lifecycle.coroutineScope"

/**
 * A [CoroutineScope] whose lifecycle matches the lifecycle of the RIB component. When the lifecycle
 * completes, the scope is cancelled.
 *
 * If this property is called before the lifecycle is active, it throws
 * [LifecycleNotStartedException]. Likewise, if called after lifecycle is ended, it throws
 * [LifecycleEndedException]. When calling this property on a worker thread, it is possible the
 * scope gets cancelled before being delivered to the caller.
 *
 * ### Caching policy
 *
 * The [CoroutineScope] instance is cached. Calls to this property either creates and caches a new
 * instance, or returns the previously cached instance.
 *
 * When the scope cancels, it is cleared from the cache.
 */
@OptIn(InternalRibsApi::class)
public val RibLifecycle<*>.coroutineScope: CoroutineScope
  get() {
    require(this is CloseableOwner) {
      "RibLifecycle.coroutineScope cannot be used by custom implementations of RibLifecycle"
    }
    ensureAlive()
    return getCloseableOrPut(SCOPE_REF_KEY) {
      CloseableCoroutineScope(
        SupervisorJob() +
          RibDispatchers.Main.immediate +
          CoroutineName("${this::class.simpleName}:coroutineScope") +
          (RibCoroutinesConfig.exceptionHandler ?: EmptyCoroutineContext),
      )
    }
  }

/** An alias for [RibLifecycle.coroutineScope], for convenience. */
@Suppress("DEPRECATION_ERROR")
@OptIn(InternalRibsApi::class)
public val RibLifecycleOwner<*>.coroutineScope: CoroutineScope
  get() = actualRibLifecycle.coroutineScope

private class CloseableCoroutineScope(override val coroutineContext: CoroutineContext) :
  CoroutineScope, Closeable {
  override fun close() {
    cancel()
  }
}
