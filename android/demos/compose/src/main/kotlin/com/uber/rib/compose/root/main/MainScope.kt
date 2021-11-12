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
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.uber.rib.compose.root.main.logged_in.LoggedInScope
import com.uber.rib.compose.root.main.logged_out.LoggedOutScope
import com.uber.rib.compose.util.AnalyticsClient
import com.uber.rib.compose.util.ExperimentClient
import com.uber.rib.compose.util.LoggerClient
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.RibActivity
import motif.Expose

@motif.Scope
interface MainScope {
  fun router(): MainRouter

  fun loggedOutScope(slot: MutableState<(@Composable () -> Unit)>): LoggedOutScope

  fun loggedInScope(slot: MutableState<(@Composable () -> Unit)>, authInfo: AuthInfo):
    LoggedInScope

  @motif.Objects
  abstract class Objects {
    abstract fun router(): MainRouter

    abstract fun interactor(): MainInteractor

    fun presenter(
      childContent: MainRouter.ChildContent,
      analyticsClient: AnalyticsClient,
      experimentClient: ExperimentClient,
      loggerClient: LoggerClient
    ): ComposePresenter {
      return object : ComposePresenter() {
        override val composable = @Composable {
          MainView(childContent)
        }
      }
    }

    fun view(
      parentViewGroup: ViewGroup,
      activity: RibActivity,
      presenter: ComposePresenter
    ): ComposeView {
      return ComposeView(parentViewGroup.context).apply {
        ViewTreeLifecycleOwner.set(this, activity)
        ViewTreeSavedStateRegistryOwner.set(this, activity)
      }
    }

    abstract fun childContent(): MainRouter.ChildContent

    @Expose
    abstract fun authStream(): AuthStream
  }
}
