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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.uber.rib.compose.root.main.logged_in.LoggedInRouter
import com.uber.rib.compose.root.main.logged_out.LoggedOutRouter
import com.uber.rib.core.BasicViewRouter

class MainRouter(
  view: ComposeView,
  interactor: MainInteractor,
  private val scope: MainScope,
  private val childContent: MainRouter.ChildContent
) : BasicViewRouter<ComposeView, MainInteractor>(view, interactor) {

  private var loggedOutRouter: LoggedOutRouter? = null
  private var loggedInRouter: LoggedInRouter? = null

  internal fun attachLoggedOut() {
    if (loggedOutRouter == null) {
      loggedOutRouter = scope.loggedOutScope(view).router().also {
        attachChild(it)
        childContent.fullScreenContent = it.presenter.composable
      }
    }
  }

  internal fun attachLoggedIn(authInfo: AuthInfo) {
    if (loggedInRouter == null) {
      loggedInRouter = scope.loggedInScope(view, authInfo).router().also {
        attachChild(it)
        childContent.fullScreenContent = it.presenter.composable
      }
    }
  }

  internal fun detachLoggedOut() {
    loggedOutRouter?.let {
      childContent.fullScreenContent = null
      detachChild(it)
    }
    loggedOutRouter = null
  }

  internal fun detachLoggedIn() {
    loggedInRouter?.let {
      childContent.fullScreenContent = null
      detachChild(it)
    }
    loggedInRouter = null
  }

  class ChildContent {
    internal var fullScreenContent: (@Composable () -> Unit)? by mutableStateOf(null)
  }
}
