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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.uber.rib.compose.root.main.AuthInfo
import com.uber.rib.compose.util.EventStream
import com.uber.rib.compose.util.StateStream
import com.uber.rib.core.ComposePresenter

@motif.Scope
interface TicTacToeScope {
  fun router(): TicTacToeRouter

  @motif.Objects
  abstract class Objects {
    abstract fun router(): TicTacToeRouter

    abstract fun interactor(): TicTacToeInteractor

    fun presenter(stateStream: StateStream<TicTacToeViewModel>, eventStream: EventStream<TicTacToeEvent>): ComposePresenter {
      return object : ComposePresenter() {
        override val composable = @Composable {
          TicTacToeView(stateStream.observe().collectAsState(initial = stateStream.current()), eventStream)
        }
      }
    }

    fun eventStream() = EventStream<TicTacToeEvent>()

    fun stateStream(authInfo: AuthInfo) = StateStream(TicTacToeViewModel(authInfo.playerOne, Board()))
  }
}
