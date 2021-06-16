package com.uber.rib.compose.root.main.logged_in.product_selection

import com.uber.rib.core.BasicComposeRouter
import com.uber.rib.core.ComposePresenter

class ProductSelectionRouter(
    presenter: ComposePresenter,
  interactor: ProductSelectionInteractor
) : BasicComposeRouter<ProductSelectionInteractor>(presenter, interactor)
