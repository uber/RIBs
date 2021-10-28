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
package com.uber.rib.compose.root

import com.uber.rib.compose.root.main.MainRouter
import com.uber.rib.core.BasicViewRouter

class RootRouter(
  view: RootView,
  interactor: RootInteractor,
  private val scope: RootScope
) : BasicViewRouter<RootView, RootInteractor>(view, interactor) {

  private var mainRouter: MainRouter? = null

  override fun willAttach() {
    attachMain()
  }

  override fun willDetach() {
    detachMain()
  }

  private fun attachMain() {
    if (mainRouter == null) {
      mainRouter = scope.mainScope(view).router().also {
        attachChild(it)
      }
    }
  }

  private fun detachMain() {
    mainRouter?.let {
      detachChild(it)
    }
  }
}
