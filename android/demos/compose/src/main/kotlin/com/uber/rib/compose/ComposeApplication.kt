package com.uber.rib.compose

import android.app.Application
import com.uber.rib.compose.client.AnalyticsClient
import com.uber.rib.compose.client.AnalyticsClientImpl
import com.uber.rib.compose.client.ExperimentClient
import com.uber.rib.compose.client.ExperimentClientImpl
import com.uber.rib.compose.client.LoggerClient
import com.uber.rib.compose.client.LoggerClientImpl

class ComposeApplication : Application() {
  val analyticsClient: AnalyticsClient by lazy { AnalyticsClientImpl(this) }
  val experimentClient: ExperimentClient by lazy { ExperimentClientImpl(this) }
  val loggerClient: LoggerClient by lazy { LoggerClientImpl(this) }
}
