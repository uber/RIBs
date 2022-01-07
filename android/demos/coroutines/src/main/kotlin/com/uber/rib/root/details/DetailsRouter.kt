package com.uber.rib.root.details

import com.uber.rib.core.Router
import com.uber.rib.core.ViewRouter
import com.uber.rib.root.details.DetailsBuilder
import com.uber.rib.root.details.DetailsInteractor
import com.uber.rib.root.details.DetailsView

class DetailsRouter(
        view: DetailsView,
        interactor: DetailsInteractor,
        component: DetailsBuilder.Component) : ViewRouter<DetailsView, DetailsInteractor>(view, interactor, component) {

}