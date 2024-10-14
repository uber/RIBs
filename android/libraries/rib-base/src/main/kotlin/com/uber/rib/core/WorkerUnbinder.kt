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

/**
 * API for unbinding a [Worker] before currently bound lifecycle has ended. Use this if you need to
 * stop your [Worker] before the [Interactor] becomes inactive for example.
 */
@Deprecated(
  message =
    """
      com.uber.rib.core.Worker is deprecated in favor of com.uber.rib.core.RibCoroutineWorker
    """
)
public fun interface WorkerUnbinder {
  /** Unbind from bound lifecycle and end worker's lifecycle. */
  public fun unbind()
}
