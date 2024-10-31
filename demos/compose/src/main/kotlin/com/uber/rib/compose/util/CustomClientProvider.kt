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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val AnalyticsLocal = staticCompositionLocalOf<AnalyticsClient> { NoOpAnalyticsClient }
val ExperimentsLocal = staticCompositionLocalOf<ExperimentClient> { NoOpExperimentClient }
val LoggerLocal = staticCompositionLocalOf<LoggerClient> { NoOpLoggerClient }

@Composable
fun CustomClientProvider(
  analyticsClient: AnalyticsClient,
  experimentClient: ExperimentClient,
  loggerClient: LoggerClient,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    AnalyticsLocal provides analyticsClient,
    ExperimentsLocal provides experimentClient,
    LoggerLocal provides loggerClient,
    content = content,
  )
}
