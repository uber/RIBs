package com.uber.rib.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.uber.rib.compose.client.*

val AnalyticsLocal = staticCompositionLocalOf<AnalyticsClient> { NoOpAnalyticsClient }
val ExperimentsLocal = staticCompositionLocalOf<ExperimentClient> { NoOpExperimentClient }
val LoggerLocal = staticCompositionLocalOf<LoggerClient> { NoOpLoggerClient }

@Composable
fun PresidioApp(
  analyticsClient: AnalyticsClient,
  experimentClient: ExperimentClient,
  loggerClient: LoggerClient,
  content: @Composable () -> Unit
) {
  CompositionLocalProvider(
    AnalyticsLocal provides analyticsClient,
    ExperimentsLocal provides experimentClient,
    LoggerLocal provides loggerClient,
    content = content
  )
}
