package com.uber.rib.compose.root.main

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.uber.rib.compose.R
import com.uber.rib.compose.root.main.logged_in.LoggedInRouter
import com.uber.rib.compose.root.main.logged_out.LoggedOutRouter
import com.uber.rib.core.*

class MainRouter(
  view: ComposeView,
  interactor: MainInteractor,
  private val scope: MainScope
) : BasicViewRouter<ComposeView, MainInteractor>(view, interactor) {

  private var loggedOutRouter: LoggedOutRouter? = null
  private var loggedInRouter: LoggedInRouter? = null

  internal fun attachLoggedOut() {
    if (loggedOutRouter == null) {
      loggedOutRouter = scope.loggedOutScope(view).router().also {
        attachChild(it)
        view.findViewById<ViewGroup>(R.id.login_logout_container).addView(it.view)
      }
    }
  }

  internal fun attachLoggedIn() {
    if (loggedInRouter == null) {
      loggedInRouter = scope.loggedInScope(view).router().also {
        attachChild(it)
        view.findViewById<ViewGroup>(R.id.login_logout_container).addView(it.view)
      }
    }
  }

  internal fun detachLoggedOut() {
    loggedOutRouter?.let {
      this@MainRouter.view.findViewById<ViewGroup>(R.id.login_logout_container).removeView(it.view)
      detachChild(it)
    }
    loggedOutRouter = null
  }

  internal fun detachLoggedIn() {
    loggedInRouter?.let {
      this@MainRouter.view.findViewById<ViewGroup>(R.id.login_logout_container).removeView(it.view)
      detachChild(it)
    }
    loggedInRouter = null
  }
}
