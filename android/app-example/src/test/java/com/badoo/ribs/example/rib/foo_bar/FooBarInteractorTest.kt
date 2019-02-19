package com.badoo.ribs.example.rib.foo_bar

import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature
import com.nhaarman.mockitokotlin2.mock
import com.uber.rib.core.InteractorHelper
import com.uber.rib.core.RibTestBasePlaceholder
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


//@RunWith(RobolectricTestRunner::class)
class FooBarInteractorTest : RibTestBasePlaceholder() {

    private val input: ObservableSource<FooBar.Input> = mock()
    private val output: Consumer<FooBar.Output> = mock()
    private val feature: FooBarFeature = mock()
    private val router: FooBarRouter = mock()
    private lateinit var interactor: FooBarInteractor

    @Before
    fun setup() {
        interactor = FooBarInteractor(
            input = input,
            output = output,
            feature = feature
        )

        InteractorHelper.attach<FooBarRouter>(interactor, router, null)
    }

    @After
    fun tearDown() {
        InteractorHelper.detach(interactor)
    }

    /**
     * TODO: Add real tests.
     */
    @Test
    fun `an example test with some conditions should pass`() {
        throw RuntimeException("Add real tests.")
    }
}
