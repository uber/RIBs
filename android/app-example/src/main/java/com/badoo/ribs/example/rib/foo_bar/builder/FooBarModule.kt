package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarInteractor
import com.badoo.ribs.example.rib.foo_bar.FooBarRouter
import com.badoo.ribs.example.rib.foo_bar.FooBarView
import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature
import dagger.Provides

@dagger.Module
internal object FooBarModule {

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: FooBarComponent
    ): FooBarRouter =
        FooBarRouter()

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun feature(): FooBarFeature =
        FooBarFeature()

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun interactor(
        router: FooBarRouter,
        permissionRequester: PermissionRequester
    ): FooBarInteractor =
        FooBarInteractor(
            router = router,
            permissionRequester = permissionRequester
        )

    @FooBarScope
    @Provides
    @JvmStatic
    internal fun node(
        viewFactory: ViewFactory<FooBarView>,
        router: FooBarRouter,
        interactor: FooBarInteractor
    ) : Node<FooBarView> = Node(
        identifier = object : FooBar {},
        viewFactory = viewFactory,
        router = router,
        interactor = interactor
    )
}
