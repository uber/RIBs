package com.uber.rib.compose.root

import com.uber.rib.compose.root.main.MainRouter
import com.uber.rib.core.BasicViewRouter

class RootRouter(
  view: RootView,
  interactor: RootInteractor,
  private val scope: RootScope
) : BasicViewRouter<RootView, RootInteractor>(view, interactor) {

  private var mainRouter: MainRouter? = null

  override fun willAttach() {
    attachMain()
  }

  override fun willDetach() {
    detachMain()
  }

  private fun attachMain() {
    if (mainRouter == null) {
      mainRouter = scope.mainScope(view).router().also {
        attachChild(it)
        view.addView(it.view)
      }
    }
  }

  private fun detachMain() {
    mainRouter?.let {
      this@RootRouter.view.removeView(it.view)
      detachChild(it)
    }
  }
}
