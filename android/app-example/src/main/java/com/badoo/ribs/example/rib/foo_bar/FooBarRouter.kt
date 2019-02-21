package com.badoo.ribs.example.rib.foo_bar

import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.ViewFactory

class FooBarRouter(
    viewFactory: ViewFactory<FooBarView>,
    interactor: FooBarInteractor
) : BaseViewRouter<FooBarView>(viewFactory, interactor)
