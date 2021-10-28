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

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.uber.rib.compose.root.main.logged_in.LoggedInRouter
import com.uber.rib.compose.root.main.logged_out.LoggedOutRouter
import com.uber.rib.core.BasicViewRouter

class MainRouter(
  view: ComposeView,
  interactor: MainInteractor,
  private val parentView: ViewGroup,
  private val scope: MainScope,
  private val childContent: ChildContent
) : BasicViewRouter<ComposeView, MainInteractor>(view, interactor) {

  private var loggedOutRouter: LoggedOutRouter? = null
  private var loggedInRouter: LoggedInRouter? = null

  override fun willAttach() {
    super.willAttach()
    parentView.addView(view)
  }

  override fun willDetach() {
    parentView.removeView(view)
    super.willDetach()
  }

  internal fun attachLoggedOut() {
    if (loggedOutRouter == null) {
      loggedOutRouter = scope.loggedOutScope(childContent.fullScreenSlot).router().also {
        attachChild(it)
      }
    }
  }

  internal fun attachLoggedIn(authInfo: AuthInfo) {
    if (loggedInRouter == null) {
      loggedInRouter =
        scope.loggedInScope(childContent.fullScreenSlot, authInfo).router().also {
          attachChild(it)
        }
    }
  }

  internal fun detachLoggedOut() {
    loggedOutRouter?.let {
      detachChild(it)
    }
    loggedOutRouter = null
  }

  internal fun detachLoggedIn() {
    loggedInRouter?.let {
      detachChild(it)
    }
    loggedInRouter = null
  }

  class ChildContent {
    internal var fullScreenSlot: MutableState<(@Composable () -> Unit)> = mutableStateOf({})
  }
}
