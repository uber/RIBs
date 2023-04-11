/*
 * Copyright (C) 2017. Uber Technologies
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
import com.uber.rib.core.lifecycle.WorkerEvent
import io.reactivex.CompletableSource
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.rxCompletable

/** [ScopeProvider] for [Worker] instances.  */
public open class WorkerScopeProvider internal constructor(
  private val workerLifecycle: Flow<WorkerEvent>,
) : ScopeProvider {
  internal constructor(workerLifecycleObservable: Observable<WorkerEvent>) : this(workerLifecycleObservable.asFlow())

  override fun requestScope(): CompletableSource {
    return rxCompletable(RibDispatchers.Unconfined) {
      workerLifecycle.take(2).collect()
    }
  }
}
