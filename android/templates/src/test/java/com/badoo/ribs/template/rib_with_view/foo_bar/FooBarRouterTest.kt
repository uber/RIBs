package com.badoo.ribs.template.rib_with_view.foo_bar

import com.nhaarman.mockitokotlin2.mock
import com.uber.rib.core.RibTestBasePlaceholder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class FooBarRouterTest : RibTestBasePlaceholder() {

    private var interactor: FooBarInteractor = mock()
    private var router: FooBarRouter? = null

    @Before
    fun setup() {
        router = FooBarRouter()
    }

    @After
    fun tearDown() {
    }

    /**
     * TODO: Add real tests.
     */
    @Test
    fun `an example test with some conditions should pass`() {
        throw RuntimeException("Add real tests.")
    }
}
