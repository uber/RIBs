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
package com.uber.rib.compose.root.main.logged_in

import com.uber.rib.core.RibDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class ScoreStream(playerOne: String, playerTwo: String) {

  private val _scoresFlow = MutableStateFlow(
    mapOf(
      playerOne to 0,
      playerTwo to 0
    )
  )
  private val scoresFlow = _scoresFlow.asStateFlow()

  suspend fun addVictory(userName: String) = withContext(RibDispatchers.Default) {
    _scoresFlow.update { scores ->
      scores.toMutableMap().apply {
        if (userName in scores) {
          set(userName, getValue(userName) + 1)
        }
      }.toMap()
    }
  }

  fun scores() = scoresFlow
}
