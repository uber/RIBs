package com.badoo.ribs.example.rib.foo_bar

import com.uber.rib.core.Router
import com.uber.rib.core.ViewFactory

class FooBarRouter(
    viewFactory: ViewFactory<FooBarView>,
    interactor: FooBarInteractor
) : Router<FooBarView>(viewFactory, interactor)
