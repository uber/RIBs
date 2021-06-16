package com.uber.rib.compose.root2

import androidx.compose.ui.platform.ComposeView
import com.uber.rib.compose.root.main.MainRouter
import com.uber.rib.core.BasicViewRouter

class RootRouter(
  view: ComposeView,
  interactor: RootInteractor
) : BasicViewRouter<ComposeView, RootInteractor>(view, interactor)
