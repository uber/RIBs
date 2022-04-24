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
package com.uber.rib.compose.root.main.logged_in.tic_tac_toe

import com.uber.rib.compose.root.main.AuthInfo
import com.uber.rib.compose.util.EventStream
import com.uber.rib.compose.util.StateStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TicTacToeInteractor(
  presenter: ComposePresenter,
  private val authInfo: AuthInfo,
  private val eventStream: EventStream<TicTacToeEvent>,
  private val stateStream: StateStream<TicTacToeViewModel>,
  private val listener: Listener
) : BasicInteractor<ComposePresenter, TicTacToeRouter>(presenter) {

  var currentPlayer: Board.MarkerType = Board.MarkerType.CROSS

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)

    eventStream.observe()
      .onEach {
        when (it) {
          is TicTacToeEvent.BoardClick -> {
            val board: Board = stateStream.current().board
            val coord = it.coordinate

            if (board.cells[coord.x][coord.y] == null) {
              if (currentPlayer == Board.MarkerType.CROSS) {
                board.cells[coord.x][coord.y] = Board.MarkerType.CROSS
                board.currentRow = coord.x
                board.currentCol = coord.y
                currentPlayer = Board.MarkerType.NOUGHT
              } else {
                board.cells[coord.x][coord.y] = Board.MarkerType.NOUGHT
                board.currentRow = coord.x
                board.currentCol = coord.y
                currentPlayer = Board.MarkerType.CROSS
              }
            }

            if (board.hasWon(Board.MarkerType.CROSS)) {
              listener.onGameWon(authInfo.playerOne)
            } else if (board.hasWon(Board.MarkerType.NOUGHT)) {
              listener.onGameWon(authInfo.playerTwo)
            } else if (board.isDraw()) {
              listener.onGameWon(null)
            }

            val newPlayerName = if (currentPlayer == Board.MarkerType.CROSS) {
              authInfo.playerOne
            } else {
              authInfo.playerTwo
            }

            stateStream.dispatch(
              stateStream.current().copy(
                board = board,
                currentPlayer = newPlayerName
              )
            )
          }
          TicTacToeEvent.XpButtonClick -> TODO("Go somewhere")
        }
      }.launchIn(coroutineScope)
  }

  interface Listener {
    fun onGameWon(winnerName: String?)
  }
}
