/*
 * Copyright (C) 2023. Uber Technologies
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
package com.uber.rib.workers.root.main

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.ComposeView
import com.uber.rib.core.ComposePresenter
import com.uber.rib.workers.root.main.ribworkerselection.RibWorkerSelectionScope

@motif.Scope
interface MainScope {
  fun router(): MainRouter

  fun ribWorkerSelectionScope(slot: MutableState<(@Composable () -> Unit)>): RibWorkerSelectionScope

  @motif.Objects
  abstract class Objects {
    abstract fun router(): MainRouter

    abstract fun interactor(): MainInteractor

    fun presenter(
      childContent: MainRouter.ChildContent,
    ): ComposePresenter {
      return object : ComposePresenter() {
        override val composable = @Composable { MainView(childContent) }
      }
    }

    fun view(parentViewGroup: ViewGroup): ComposeView {
      return ComposeView(parentViewGroup.context)
    }

    abstract fun childContent(): MainRouter.ChildContent
  }
}
