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
package com.uber.rib.core.lifecycle.internal

import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleNotStartedException
import com.uber.rib.core.InternalRibsApi
import com.uber.rib.core.lifecycle.RibLifecycle
import com.uber.rib.core.lifecycle.RibLifecycleOwner
import com.uber.rib.core.setIfNullAndGet
import java.io.Closeable
import kotlin.reflect.KMutableProperty0
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

/** An internal implementation of [RibLifecycle] and [CloseableOwner]. */
@InternalRibsApi
public open class InternalRibLifecycle<T : Comparable<T>>(
  override val lifecycleRange: ClosedRange<T>,
) : RibLifecycle<T>, CloseableOwner {
  override val lifecycleFlow: MutableSharedFlow<T> =
    MutableSharedFlow(1, 0, BufferOverflow.DROP_OLDEST)
  override val closeables: MutableMap<String, Closeable> = mutableMapOf()
}

@Suppress("NOTHING_TO_INLINE", "USELESS_ELVIS", "DEPRECATION", "RedundantRequireNotNullCall")
@InternalRibsApi
internal inline fun <T : Comparable<T>> RibLifecycleOwner<T>.actualRibLifecycle(
  prop: KMutableProperty0<RibLifecycle<T>?>,
  range: ClosedRange<T>,
): RibLifecycle<T> =
  // open fields will be null in mocking, unless mocked.
  // fields initialized in constructor will also be null, even when type is non-null.
  ribLifecycle
    ?: run {
      checkNotNull(lifecycleFlow) { "When mocking, mock 'ribLifecycle'" }
      prop.setIfNullAndGet {
        object : RibLifecycle<T>, CloseableOwner {
          override val lifecycleFlow = this@actualRibLifecycle.lifecycleFlow
          override val lifecycleRange = range
          override val closeables: MutableMap<String, Closeable> = mutableMapOf()
        }
      }
    }

@InternalRibsApi
public interface CloseableOwner {
  public val closeables: MutableMap<String, Closeable>
}

@InternalRibsApi
internal inline fun <reified T : Closeable, R> R.getCloseableOrPut(
  key: String,
  closeable: () -> T,
): T where R : CloseableOwner, R : RibLifecycle<*> {
  val result = synchronized(closeables) { closeables.getOrPut(key, closeable) }
  check(result is T) {
    "Closeables map already had a value for key $key, but it was not of type ${T::class.simpleName}"
  }
  invokeOnLifecycleEnded { result.close() }
  return result
}

@InternalRibsApi
@OptIn(DelicateCoroutinesApi::class)
private inline fun <T : Comparable<T>> RibLifecycle<T>.invokeOnLifecycleEnded(
  crossinline block: () -> Unit,
) {
  GlobalScope.launch(DirectDispatcher) {
    try {
      lifecycleFlow.takeWhile { it < lifecycleRange.endInclusive }.collect()
    } finally {
      block()
    }
  }
}

@JvmSynthetic
internal fun <T : Comparable<T>> RibLifecycle<T>.ensureAlive() {
  lifecycleFlow.ensureAlive(lifecycleRange)
}

@JvmSynthetic
internal fun <T : Comparable<T>> SharedFlow<T>.ensureAlive(range: ClosedRange<out T>) {
  val lastEmitted = replayCache.lastOrNull()
  when {
    lastEmitted == null || lastEmitted < range.start -> throw LifecycleNotStartedException()
    lastEmitted >= range.endInclusive -> throw LifecycleEndedException()
  }
}
