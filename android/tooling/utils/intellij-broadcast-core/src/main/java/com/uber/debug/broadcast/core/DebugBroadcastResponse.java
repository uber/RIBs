/*
 * Copyright (C) 2021. Uber Technologies
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
package com.uber.debug.broadcast.core;

import androidx.annotation.Nullable;

/*
 * Data class for broadcast receiver responses.
 */
public class DebugBroadcastResponse<T> {

  public static int VERSION = 1;

  Boolean success;
  int version;
  @Nullable private String errorDescription;
  @Nullable private T payload;

  public DebugBroadcastResponse(T payload) {
    this.success = true;
    this.version = VERSION;
    this.errorDescription = null;
    this.payload = payload;
  }

  public void setErrorDescription(String errorDescription) {
    this.success = false;
    this.errorDescription = errorDescription;
  }

  @Nullable
  public T getPayload() {
    return payload;
  }
}
