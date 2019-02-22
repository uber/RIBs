package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.example.rib.foo_bar.FooBar.Input
import com.badoo.ribs.example.rib.foo_bar.FooBar.Output
import com.badoo.ribs.example.rib.foo_bar.FooBarInteractor
import com.badoo.ribs.example.rib.foo_bar.FooBarRouter
import com.badoo.ribs.example.rib.foo_bar.FooBarView
import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature
import com.uber.rib.core.ViewFactory
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object FooBarModule {

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun interactor(
        input: ObservableSource<Input>,
        output: Consumer<Output>,
        feature: FooBarFeature
    ): FooBarInteractor =
        FooBarInteractor(
            input = input,
            output = output,
            feature = feature
        )

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: FooBarComponent,
        viewFactory: ViewFactory<FooBarView>,
        interactor: FooBarInteractor
    ): FooBarRouter =
        FooBarRouter(
            viewFactory = viewFactory,
            interactor = interactor
        )

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun feature(): FooBarFeature =
        FooBarFeature()
}
