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
package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
internal inline fun <T> TestScope.assertLifecycleEventEagerness(
  event: T,
  crossinline lifecycleScopeProviderProducer: () -> LifecycleScopeProvider<T>,
) {
  val events = mutableListOf<Event<T>>()
  var disposable: Disposable? = null
  launch(Dispatchers.Unconfined) {
    disposable =
      lifecycleScopeProviderProducer().lifecycle().subscribe { lifecycleEvent ->
        events.add(Event.Lifecycle(lifecycleEvent))
      }
    events.add(Event.AfterSubscription)
  }
  advanceUntilIdle()
  assertThat(events).isEqualTo(listOf(Event.Lifecycle(event), Event.AfterSubscription))
  disposable?.dispose()
}

@PublishedApi
internal sealed interface Event<out T> {
  data class Lifecycle<T>(val event: T) : Event<T>
  object AfterSubscription : Event<Nothing>
}
