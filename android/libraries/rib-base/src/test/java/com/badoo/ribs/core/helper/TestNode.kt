package com.badoo.ribs.core.helper

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.nhaarman.mockitokotlin2.mock

class TestNode(
    identifier: Rib
) : Node<TestView>(
    identifier = identifier,
    viewFactory = mock(),
    router = mock(),
    interactor = mock()
) {
    var handleBackPress: Boolean =
        false

    var handleBackPressInvoked: Boolean =
        false

    override fun handleBackPress(): Boolean =
        handleBackPress.also {
            handleBackPressInvoked = true
        }
}
