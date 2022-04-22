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
package com.uber.rib.compose.root.main.logged_in.off_game

import com.uber.rib.compose.root.main.logged_in.ScoreStream
import com.uber.rib.compose.util.EventStream
import com.uber.rib.compose.util.StateStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OffGameInteractor(
  presenter: ComposePresenter,
  private val eventStream: EventStream<OffGameEvent>,
  private val stateStream: StateStream<OffGameViewModel>,
  private val scoreStream: ScoreStream,
  private val listener: Listener
) : BasicInteractor<ComposePresenter, OffGameRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)

    eventStream.observe()
      .onEach {
        when (it) {
          is OffGameEvent.StartGame -> {
            listener.onStartGame()
          }
        }
      }.launchIn(coroutineScope)

    scoreStream.scores()
      .onEach {
        val currentState = stateStream.current()
        stateStream.dispatch(
          currentState.copy(
            playerOneWins = it[currentState.playerOne] ?: 0,
            playerTwoWins = it[currentState.playerTwo] ?: 0,
          )
        )
      }.launchIn(coroutineScope)
  }

  interface Listener {
    fun onStartGame()
  }
}
