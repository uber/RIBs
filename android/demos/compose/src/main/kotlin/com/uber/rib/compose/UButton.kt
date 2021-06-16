package com.uber.rib.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun UButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  analyticsId: String? = null,
  content: @Composable RowScope.() -> Unit
) {
  val analyticsClient = AnalyticsLocal.current
  val onClickWrapper: () -> Unit = {
    analyticsId?.let { analyticsClient.trackClick(it) }
    onClick()
  }
  Button(
    onClick = onClickWrapper,
    modifier = modifier,
    content = content
  )
  LaunchedEffect(null) {
    analyticsId?.let { analyticsClient.trackImpression(it) }
  }
}
