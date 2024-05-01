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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers

/**
 * Config object to specify global overrides for Rib Coroutine behavior including Dispatchers and
 * exception handling
 */
public object RibCoroutinesConfig {
  /**
   * Specify [RibDispatchersProvider] that provide default [CoroutineDispatcher]'s for Rib based
   * scopes. Defaults to standard [Dispatchers]. Useful in areas where injecting Dispatchers is not
   * ideal, such as Test.
   */
  @JvmStatic public var dispatchers: RibDispatchersProvider = DefaultRibDispatchers()

  /**
   * Specify [CoroutineExceptionHandler] to be used with Rib based scopes. Defaults to throwing
   * exception. Useful for specifying additional information before passed to
   * [Thread.UncaughtExceptionHandler].
   */
  @JvmStatic public var exceptionHandler: CoroutineExceptionHandler? = null

  /**
   * Specify the [CoroutineDispatcher] to be used while binding a [com.uber.rib.Worker] via
   * [WorkerBinder]
   *
   * IMPORTANT: This shouldn't be used outside [WorkerBinder] given that [WorkerBinder]/[Worker] are
   * deprecated
   */
  @Deprecated(
    message =
      """
      This dispatcher is only intended to be used within the [com.uber.rib.core.WorkerBinder].
      For adding and binding new RIB workers please use [RibCoroutineWorker]
      """,
  )
  @JvmStatic
  public var deprecatedWorkerDispatcher: CoroutineDispatcher = RibDispatchers.Unconfined
}
