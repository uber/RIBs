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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uber.rib.workers.util.EventStream

@Composable
fun RibWorkerSelectionView(
  viewModel: State<RibWorkerSelectionViewModel>,
  eventStream: EventStream<RibWorkerBindTypeClickType>,
) {
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
  ) {
    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.SINGLE_WORKER_BIND_CALLER_THREAD,
      "Bind Single Worker on current caller thread",
    )

    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.SINGLE_WORKER_BIND_BACKGROUND_THREAD,
      "Bind single Worker off main thread",
    )

    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.BIND_MULTIPLE_DEPRECATED_WORKERS,
      "Bind multiple Deprecated Workers",
    )

    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.BIND_MULTIPLE_RIB_COROUTINE_WORKERS,
      "Bind multiple RibCoroutineWorkers",
    )

    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.BIND_RIB_COROUTINE_WORKER,
      "Bind RibCoroutineWorker",
    )

    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.WORKER_TO_RIB_COROUTINE_WORKER,
      "Worker <> RibCoroutineWorker",
    )

    AddButton(
      eventStream,
      RibWorkerBindTypeClickType.RIB_COROUTINE_WORKER_TO_WORKER,
      "RibCoroutineWorker <> Worker",
    )

    Text(
      viewModel.value.workerInfo,
      modifier = Modifier.fillMaxSize(),
    )
  }
}

@Composable
fun AddButton(
  eventStream: EventStream<RibWorkerBindTypeClickType>,
  ribWorkerBindTypeClickType: RibWorkerBindTypeClickType,
  buttonText: String,
) {
  Button(
    colors =
      ButtonDefaults.buttonColors(
        backgroundColor = Color.Black,
        contentColor = Color.White,
      ),
    onClick = { eventStream.notify(ribWorkerBindTypeClickType) },
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(text = buttonText)
  }
}

@Preview
@Composable
fun RibWorkerSelectionViewDemo() {
  val demoText =
    "WorkerBinderInfo(workerName=com.uber.rib.workers.root.main.workers.IoWorker, workerEvent=START, coroutineContext=Dispatchers.IO, threadName=DefaultDispatcher-worker-4, totalBindingDurationMilli=107)\n"
  val viewModel = remember { mutableStateOf(RibWorkerSelectionViewModel(demoText)) }
  RibWorkerSelectionView(viewModel, EventStream())
}
