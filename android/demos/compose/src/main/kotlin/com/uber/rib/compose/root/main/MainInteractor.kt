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

import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainInteractor(
  presenter: ComposePresenter,
  private val authStream: AuthStream,
  private val childContent: MainRouter.ChildContent
) : BasicInteractor<ComposePresenter, MainRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)

    router.view.setContent { MainView(childContent = childContent) }
    authStream.observe()
      .onEach {
        if (it.isLoggedIn) {
          router.detachLoggedOut()
          router.attachLoggedIn(it)
        } else {
          router.detachLoggedIn()
          router.attachLoggedOut()
        }
      }.launchIn(coroutineScope)
  }
}
