package com.badoo.ribs.example.rib.foo_bar

import com.uber.rib.core.Node
import com.uber.rib.core.ViewFactory

class FooBarNode(
    viewFactory: ViewFactory<FooBarView>,
    interactor: FooBarInteractor
) : Node<FooBarView>(viewFactory, interactor)
