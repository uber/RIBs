package com.uber.rib.compose.root2

import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.EmptyPresenter

class RootInteractor(presenter: EmptyPresenter) : BasicInteractor<EmptyPresenter, RootRouter>(presenter) {
  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
    router.view.setContent {
      RootView()
    }
  }
}
