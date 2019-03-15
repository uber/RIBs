package com.badoo.ribs.example.rib.foo_bar

import com.badoo.ribs.android.PermissionRequester
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class FooBarInteractorTest {

    private val router: FooBarRouter = mock()
    private val permissionRequester: PermissionRequester = mock()
    private lateinit var interactor: FooBarInteractor

    @Before
    fun setup() {
        interactor = FooBarInteractor(
            router = router,
            permissionRequester = permissionRequester
        )
    }

    @After
    fun tearDown() {
    }

    /**
     * TODO: Add real tests.
     */
    @Test
    fun `an example test with some conditions should pass`() {
    }
}
