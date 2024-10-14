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
package com.uber.rib.compose.root.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthStream {
  private val mutableAuthFlow = MutableStateFlow(AuthInfo(false, "", ""))
  private val authFlow = mutableAuthFlow.asStateFlow()

  fun observe() = authFlow

  fun accept(value: AuthInfo) {
    mutableAuthFlow.update { value }
  }
}

data class AuthInfo(
  val isLoggedIn: Boolean,
  val playerOne: String = "",
  val playerTwo: String = "",
)
