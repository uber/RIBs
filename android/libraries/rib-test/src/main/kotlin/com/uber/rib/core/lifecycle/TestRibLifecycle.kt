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
@file:OptIn(InternalRibsApi::class)

package com.uber.rib.core.lifecycle

import com.uber.rib.core.InternalRibsApi
import com.uber.rib.core.lifecycle.internal.CloseableOwner
import java.io.Closeable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/** A test implementation of [RibLifecycle]. */
public class TestRibLifecycle<T : Comparable<T>>(
  override val lifecycleRange: ClosedRange<T>,
) : RibLifecycle<T>, CloseableOwner {
  override val lifecycleFlow: MutableSharedFlow<T> =
    MutableSharedFlow(1, 0, BufferOverflow.DROP_OLDEST)
  override val closeables: MutableMap<String, Closeable> = mutableMapOf()
}

/** Emits [event] into the lifecycle. */
public fun <T : Comparable<T>> TestRibLifecycle<T>.emit(event: T) {
  lifecycleFlow.tryEmit(event)
}
