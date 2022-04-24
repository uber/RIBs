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
package com.uber.rib.compose.root.main.logged_in.off_game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uber.rib.compose.util.CustomButton
import com.uber.rib.compose.util.EventStream

@Composable
fun OffGameView(viewModel: State<OffGameViewModel>, eventStream: EventStream<OffGameEvent>) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
  ) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(text = viewModel.value.playerOne)
      Text(text = "Win Count: ${viewModel.value.playerOneWins}")
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(text = viewModel.value.playerTwo)
      Text(text = "Win Count: ${viewModel.value.playerTwoWins}")
    }
    CustomButton(
      analyticsId = "26882559-fc45",
      onClick = { eventStream.notify(OffGameEvent.StartGame) },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = "START GAME")
    }
  }
}

@Preview
@Composable
fun OffGameViewPreview() {
  val viewModel = remember { mutableStateOf(OffGameViewModel("James", "Alejandro", 3, 0)) }
  OffGameView(viewModel, EventStream())
}
