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
package com.uber.rib.compose.root.main.logged_out

import com.uber.rib.compose.root.main.AuthInfo
import com.uber.rib.compose.root.main.AuthStream
import com.uber.rib.compose.util.EventStream
import com.uber.rib.compose.util.StateStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoggedOutInteractor(
  presenter: ComposePresenter,
  private val authStream: AuthStream,
  private val eventStream: EventStream<LoggedOutEvent>,
  private val stateStream: StateStream<LoggedOutViewModel>
) : BasicInteractor<ComposePresenter, LoggedOutRouter>(presenter) {
  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
    eventStream.observe()
      .onEach {
        when (it) {
          is LoggedOutEvent.PlayerNameChanged -> {
            with(stateStream) {
              dispatch(
                current().copy(
                  playerOne = if (it.num == 1) it.name else current().playerOne,
                  playerTwo = if (it.num == 2) it.name else current().playerTwo
                )
              )
            }
          }
          LoggedOutEvent.LogInClick -> {
            val currentState = stateStream.current()
            authStream.accept(AuthInfo(true, currentState.playerOne, currentState.playerTwo))
          }
        }
      }.launchIn(coroutineScope)
  }
}
