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

/** Class representing a response. */
public open class Response<T> {
  /** Whether request was successful */
  public var success: Boolean = false

  /** Protocol version of the response message */
  public var version: Int = 0

  /** Description of the error (if any) */
  public var errorDescription: String? = null

  /** Payload of the response */
  public var payload: T? = null
}
