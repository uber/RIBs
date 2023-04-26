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

/** [ScopeProvider] for [Worker] instances. */
public open class WorkerScopeProvider internal constructor(delegate: ScopeProvider) :
  ScopeProvider by delegate {
  internal constructor(lifecycle: Observable<WorkerEvent>) : this(lifecycle.asScopeProvider())
}

private fun Observable<*>.asScopeProvider() = asCompletableSource().asScopeProvider()

private fun Observable<*>.asCompletableSource() = skip(1).firstElement().ignoreElement()

private fun CompletableSource.asScopeProvider() = ScopeProvider { this }
