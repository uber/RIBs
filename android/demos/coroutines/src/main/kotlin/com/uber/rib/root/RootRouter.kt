package com.uber.rib.root

import android.view.View

import com.uber.rib.core.ViewRouter
import com.uber.rib.root.details.DetailsBuilder
import com.uber.rib.root.details.DetailsRouter

/**
 * Adds and removes children of {@link RootBuilder.RootScope}.
 *
 * TODO describe the possible child configurations of this scope.
 */
class RootRouter(
        view: RootView,
        interactor: RootInteractor,
        component: RootBuilder.Component,
        val detailsBuilder: DetailsBuilder,
) : ViewRouter<RootView, RootInteractor>(view, interactor, component) {

    var detailsRouter: DetailsRouter? = null

    fun attachDetails() {
        detailsRouter = detailsBuilder.build(getView())
        attachChild(detailsBuilder)
        view.addView(loggedOutRouter.view)
    }
}
