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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.uber.rib.compose.root.main.AuthInfo
import com.uber.rib.compose.root.main.logged_in.off_game.OffGameInteractor
import com.uber.rib.compose.root.main.logged_in.off_game.OffGameScope
import com.uber.rib.compose.root.main.logged_in.tic_tac_toe.TicTacToeInteractor
import com.uber.rib.compose.root.main.logged_in.tic_tac_toe.TicTacToeScope
import com.uber.rib.compose.util.EventStream
import com.uber.rib.core.ComposePresenter
import motif.Expose

@motif.Scope
interface LoggedInScope {
  fun router(): LoggedInRouter

  fun offGameScope(slot: MutableState<(@Composable (() -> Unit))>, authInfo: AuthInfo): OffGameScope

  fun ticTacToeScope(slot: MutableState<(@Composable (() -> Unit))>, authInfo: AuthInfo): TicTacToeScope

  @motif.Objects
  abstract class Objects {
    abstract fun router(): LoggedInRouter

    abstract fun interactor(): LoggedInInteractor

    abstract fun childContent(): LoggedInRouter.ChildContent

    fun presenter(
      eventStream: EventStream<LoggedInEvent>,
      childContent: LoggedInRouter.ChildContent
    ): ComposePresenter {
      return object : ComposePresenter() {
        override val composable = @Composable {
          LoggedInView(eventStream, childContent)
        }
      }
    }

    fun eventStream() = EventStream<LoggedInEvent>()

    @Expose
    fun scoreSteam(authInfo: AuthInfo): ScoreStream {
      return ScoreStream(authInfo.playerOne, authInfo.playerTwo)
    }

    @Expose
    abstract fun startGameListener(interactor: LoggedInInteractor): OffGameInteractor.Listener

    @Expose
    abstract fun gameWonListener(interactor: LoggedInInteractor): TicTacToeInteractor.Listener
  }
}
