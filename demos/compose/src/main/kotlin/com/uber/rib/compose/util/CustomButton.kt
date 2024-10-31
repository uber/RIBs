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
package com.uber.rib.compose.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  analyticsId: String? = null,
  content: @Composable RowScope.() -> Unit,
) {
  val analyticsClient = AnalyticsLocal.current
  val onClickWrapper: () -> Unit = {
    analyticsId?.let { analyticsClient.trackClick(it) }
    onClick()
  }
  Button(
    onClick = onClickWrapper,
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black, contentColor = Color.White),
    content = content,
  )
  LaunchedEffect(null) { analyticsId?.let { analyticsClient.trackImpression(it) } }
}
