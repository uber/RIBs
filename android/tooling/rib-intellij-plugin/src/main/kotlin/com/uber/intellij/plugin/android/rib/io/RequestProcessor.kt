/*
 * Copyright (C) 2018-2019. Uber Technologies
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
package com.uber.intellij.plugin.android.rib.io

import com.google.common.util.concurrent.ListenableFuture

/**
 * Interface used by classes capable of communicating to Android device, i.e send and receive
 * messages.
 */
public interface RequestProcessor {

  /** Send a request to device and returns a future to access response. */
  public fun <T> execute(request: Request<T>): ListenableFuture<T>
}
