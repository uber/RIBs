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
package com.uber.rib.workers.root.main.ribworkerselection

enum class RibWorkerBindTypeClickType {
  SINGLE_WORKER_BIND_CALLER_THREAD,
  SINGLE_WORKER_BIND_BACKGROUND_THREAD,
  BIND_MULTIPLE_DEPRECATED_WORKERS,
  BIND_MULTIPLE_RIB_COROUTINE_WORKERS,
  BIND_RIB_COROUTINE_WORKER,
  WORKER_TO_RIB_COROUTINE_WORKER,
  RIB_COROUTINE_WORKER_TO_WORKER,
}
