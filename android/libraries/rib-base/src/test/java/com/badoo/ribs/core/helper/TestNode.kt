package com.badoo.ribs.core.helper

import com.badoo.ribs.core.Node
import com.nhaarman.mockitokotlin2.mock

class TestNode(
    forClass: Class<*>
) : Node<TestView>(
    forClass = forClass,
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
