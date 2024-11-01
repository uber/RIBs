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
package com.uber.rib.workers.root.main.ribworkerselection

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.uber.rib.core.ComposePresenter
import com.uber.rib.workers.root.main.workers.BackgroundWorker
import com.uber.rib.workers.root.main.workers.DefaultRibCoroutineWorker
import com.uber.rib.workers.root.main.workers.DefaultWorker
import com.uber.rib.workers.root.main.workers.IoWorker
import com.uber.rib.workers.root.main.workers.UiWorker
import com.uber.rib.workers.util.EventStream
import com.uber.rib.workers.util.StateStream

@motif.Scope
interface RibWorkerSelectionScope {
  fun router(): RibWorkerSelectionRouter

  @motif.Objects
  abstract class Objects {
    abstract fun router(): RibWorkerSelectionRouter

    abstract fun interactor(): RibWorkerSelectionInteractor

    fun presenter(
      stateStream: StateStream<RibWorkerSelectionViewModel>,
      eventStream: EventStream<RibWorkerBindTypeClickType>,
    ): ComposePresenter {
      return object : ComposePresenter() {
        override val composable =
          @Composable {
            RibWorkerSelectionView(
              stateStream.observe().collectAsState(initial = stateStream.current()),
              eventStream,
            )
          }
      }
    }

    fun eventStream() = EventStream<RibWorkerBindTypeClickType>()

    fun stateStream() = StateStream(RibWorkerSelectionViewModel())

    fun defaultWorker() = DefaultWorker()

    fun backgroundWorker() = BackgroundWorker()

    fun ioWorker(application: Application) = IoWorker(application)

    fun uiWorker(application: Application) = UiWorker(application)

    fun defaultRibCoroutineWorker() = DefaultRibCoroutineWorker()
  }
}
