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
package com.uber.rib.compose.root.main.logged_out

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uber.rib.compose.util.EventStream

@Composable
fun LoggedOutView(viewModel: State<LoggedOutViewModel>, eventStream: EventStream<LoggedOutEvent>) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
  ) {
    TextField(
      value = viewModel.value.playerOne,
      onValueChange = { eventStream.notify(LoggedOutEvent.PlayerNameChanged(it, 1)) },
      placeholder = { Text(text = "Player One Name") },
      modifier = Modifier.fillMaxWidth()
    )
    TextField(
      value = viewModel.value.playerTwo,
      onValueChange = { eventStream.notify(LoggedOutEvent.PlayerNameChanged(it, 2)) },
      placeholder = { Text(text = "Player Two Name") },
      modifier = Modifier.fillMaxWidth()
    )
    Button(
      colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Black,
        contentColor = Color.White
      ),
      onClick = { eventStream.notify(LoggedOutEvent.LogInClick) },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = "LOGIN")
    }
  }
}

@Preview
@Composable
fun LoggedOutViewPreview() {
  val viewModel = remember { mutableStateOf(LoggedOutViewModel("James", "Alejandro")) }
  LoggedOutView(viewModel, EventStream())
}
