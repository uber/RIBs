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
package com.uber.rib.compose.root.main.logged_in

import com.uber.autodispose.autoDispose
import com.uber.rib.compose.root.main.AuthInfo
import com.uber.rib.compose.root.main.AuthStream
import com.uber.rib.compose.root.main.logged_in.off_game.OffGameInteractor
import com.uber.rib.compose.root.main.logged_in.tic_tac_toe.TicTacToeInteractor
import com.uber.rib.compose.util.EventStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import io.reactivex.rxkotlin.ofType

class LoggedInInteractor(
  presenter: ComposePresenter,
  private val authInfo: AuthInfo,
  private val authStream: AuthStream,
  private val eventStream: EventStream<LoggedInEvent>,
  private val scoreStream: ScoreStream
) : BasicInteractor<ComposePresenter, LoggedInRouter>(presenter),
  OffGameInteractor.Listener,
  TicTacToeInteractor.Listener {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
    eventStream.observe()
      .ofType<LoggedInEvent.LogOutClick>()
      .autoDispose(this)
      .subscribe { authStream.accept(AuthInfo(false)) }

    router.attachOffGame(authInfo)
  }

  override fun onStartGame() {
    router.detachOffGame()
    router.attachTicTacToe(authInfo)
  }

  override fun onGameWon(winner: String?) {
    if (winner != null) {
      scoreStream.addVictory(winner)
    }

    router.detachTicTacToe()
    router.attachOffGame(authInfo)
  }
}
