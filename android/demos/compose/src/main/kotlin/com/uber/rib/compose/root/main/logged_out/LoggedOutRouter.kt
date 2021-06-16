package com.uber.rib.compose.root.main.logged_out

import com.uber.rib.core.BasicViewRouter

class LoggedOutRouter(
  view: LoggedOutView,
  interactor: LoggedOutInteractor
) : BasicViewRouter<LoggedOutView, LoggedOutInteractor>(view, interactor)
