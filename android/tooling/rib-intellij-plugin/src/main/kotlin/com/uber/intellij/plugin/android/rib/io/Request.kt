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

import com.android.ddmlib.IDevice

/**
 * Class representing a request.
 *
 * @param device the device to send the request to
 * @param command the command to send
 * @param clazz the class of the expected response
 * @param params the parameter to add to request
 * @param timeoutMs the timeout to add to request
 * @param numRetries the number of retires to use for this request
 */
open class Request<T>(
  val device: IDevice,
  val command: String,
  val clazz: Class<T>,
  val params: List<Pair<String, Any>> = emptyList(),
  val timeoutMs: Int = 2000,
  val numRetries: Int = 1
)
