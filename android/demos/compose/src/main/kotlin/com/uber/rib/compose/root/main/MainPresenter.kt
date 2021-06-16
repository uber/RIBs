package com.uber.rib.compose.root.main

import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.platform.ComposeView
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.rib.compose.ComposeApplication
import com.uber.rib.compose.PresidioApp
import com.uber.rib.compose.client.AnalyticsClient
import com.uber.rib.compose.client.ExperimentClient
import com.uber.rib.compose.client.LoggerClient
import com.uber.rib.core.ViewPresenter

class MainPresenter(
  view: ComposeView,
  viewModel: MainViewModel
) : ViewPresenter<ComposeView>(view) {

  private val viewModelStream: BehaviorRelay<MainViewModel> = BehaviorRelay.createDefault(viewModel)

  // TODO: get these from DI instead of this hack
  private val analyticsClient: AnalyticsClient
    get() = (view.context.applicationContext as ComposeApplication).analyticsClient
  private val experimentClient: ExperimentClient
    get() = (view.context.applicationContext as ComposeApplication).experimentClient
  private val loggerClient: LoggerClient
    get() = (view.context.applicationContext as ComposeApplication).loggerClient

  override fun didLoad() {
    super.didLoad()
    view.setContent {
      PresidioApp(
        analyticsClient = analyticsClient,
        experimentClient = experimentClient,
        loggerClient = loggerClient
      ) {
        MainView(
          viewModel = viewModelStream.subscribeAsState(initial = viewModelStream.value!!),
          onRestartClick = { resetCount() }
        )
      }
    }
  }

  fun incrementCount() {
    with(viewModelStream) {
      accept(value?.copy(count = 1 + (value?.count ?: 0)))
    }
  }

  fun resetCount() {
    viewModelStream.accept(viewModelStream.value?.copy(count = 0))
  }
}
