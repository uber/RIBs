package com.uber.rib.compose.root.main.logged_in

import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import com.uber.rib.compose.root.main.logged_in.product_selection.ProductSelectionRouter
import com.uber.rib.core.BasicViewRouter

class LoggedInRouter(
  view: ComposeView,
  interactor: LoggedInInteractor,
  private val scope: LoggedInScope,
  private val childContent: ChildContent
) : BasicViewRouter<ComposeView, LoggedInInteractor>(view, interactor) {

  private var productSelectionRouter: ProductSelectionRouter? = null

  internal fun attachProductSelection() {
    if (productSelectionRouter == null) {
      productSelectionRouter = scope.productSelectionScope().router().also {
        attachChild(it)
        childContent.fullScreenContent = it.presenter.composable
      }
    }
  }

  internal fun detachProductSelection() {
    productSelectionRouter?.let {
      detachChild(it)
    }
    productSelectionRouter = null
    childContent.fullScreenContent = null
  }

  override fun willDetach() {
    detachProductSelection()
    super.willDetach()
  }

  class ChildContent {
    internal var fullScreenContent: (@Composable () -> Unit)? by mutableStateOf(null)
  }
}
