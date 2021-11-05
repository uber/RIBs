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
package com.uber.rib.compose.root.main.logged_in

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uber.rib.compose.util.CustomButton
import com.uber.rib.compose.util.EventStream

@Composable
fun LoggedInView(
  eventStream: EventStream<LoggedInEvent>,
  childContent: LoggedInRouter.ChildContent,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top,
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Green)
  ) {
    Text("Logged In! (Compose RIB)")
    Spacer(Modifier.height(16.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1.0f)
        .padding(4.dp)
        .background(Color.LightGray)
    ) {
      childContent.fullScreenSlot.value.invoke()
    }
    CustomButton(
      analyticsId = "8a570808-07a4",
      onClick = { eventStream.notify(LoggedInEvent.LogOutClick) },
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Text(text = "LOGOUT")
    }
  }
}

@Preview
@Composable
fun LoggedInViewPreview() {
  LoggedInView(EventStream(), LoggedInRouter.ChildContent())
}
