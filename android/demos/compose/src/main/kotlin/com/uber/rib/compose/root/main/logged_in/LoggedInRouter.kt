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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.uber.rib.compose.root.main.AuthInfo
import com.uber.rib.compose.root.main.logged_in.off_game.OffGameRouter
import com.uber.rib.compose.root.main.logged_in.tic_tac_toe.TicTacToeRouter
import com.uber.rib.core.BasicComposeRouter
import com.uber.rib.core.ComposePresenter

class LoggedInRouter(
  presenter: ComposePresenter,
  interactor: LoggedInInteractor,
  private val scope: LoggedInScope,
  private val childContent: ChildContent
) : BasicComposeRouter<LoggedInInteractor>(presenter, interactor) {

  private var offGameRouter: OffGameRouter? = null
  private var ticTacToeRouter: TicTacToeRouter? = null

  internal fun attachOffGame(authInfo: AuthInfo) {
    if (offGameRouter == null) {
      offGameRouter = scope.offGameScope(authInfo).router().also {
        attachChild(it)
        childContent.fullScreenContent = it.presenter.composable
      }
    }
  }

  internal fun attachTicTacToe(authInfo: AuthInfo) {
    if (ticTacToeRouter == null) {
      ticTacToeRouter = scope.ticTacToeScope(authInfo).router().also {
        attachChild(it)
        childContent.fullScreenContent = it.presenter.composable
      }
    }
  }

  internal fun detachOffGame() {
    offGameRouter?.let {
      detachChild(it)
    }
    offGameRouter = null
    childContent.fullScreenContent = null
  }

  internal fun detachTicTacToe() {
    ticTacToeRouter?.let {
      detachChild(it)
    }
    ticTacToeRouter = null
    childContent.fullScreenContent = null
  }

  override fun willDetach() {
    detachTicTacToe()
    super.willDetach()
  }

  class ChildContent {
    internal var fullScreenContent: (@Composable () -> Unit)? by mutableStateOf(null)
  }
}
