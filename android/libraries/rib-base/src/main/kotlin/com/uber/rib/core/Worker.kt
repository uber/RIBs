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

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface used when creating a manager or helper class that should be bound to an interactor's
 * lifecycle using a binder like [WorkerBinder]. The worker event is decoupled from the interactor's
 * actual lifecycle so that we're not stuck moving these classes around if there are other
 * lifecycles we're interested in.
 *
 * By default a [Worker] will be bound on the [CoroutineDispatcher] defined at [WorkerBinder.bind]
 * call, except when the [Worker]'s [coroutineContext] is overriden with any other value than the
 * default [EmptyCoroutineContext]. The new resulting binding dispatcher (e.g.
 * RibDispatchers.Default) from [Worker] will take priority over the one passed via a
 * [WorkerBinder.bind] call
 */
public interface Worker {

  /**
   * When overriden, will specify on which [CoroutineContext] the [Worker] will be bound via
   * [WorkerBinder] (ignoring any [CoroutineDispatcher] being passed via [WorkerBinder])
   *
   * For example given list of workers:
   * 1) workers = listOf(defaultWorker, uiWorker)
   * 2) Where [uiWorker] overrides [coroutinesContext] with [RibDispatchers.Main]
   * 3) After calling WorkerBinder.bind(interactor, workers, RibDispatchers.IO). [uiWorker] will be
   *    guaranteed to be called on RibDispatchers.Main
   */
  @JvmDefault
  public val coroutineContext: CoroutineContext
    get() = EmptyCoroutineContext

  /**
   * Called when worker is started.
   *
   * @param lifecycle The lifecycle of the worker to use for subscriptions.
   */
  @JvmDefault public fun onStart(lifecycle: WorkerScopeProvider) {}

  /** Called when the worker is stopped. */
  @JvmDefault public fun onStop() {}
}
