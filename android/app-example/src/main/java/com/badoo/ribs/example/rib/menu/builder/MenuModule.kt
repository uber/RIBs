package com.badoo.ribs.example.rib.menu.builder

import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.MenuInteractor
import com.badoo.ribs.example.rib.menu.MenuRouter
import com.badoo.ribs.example.rib.menu.MenuView
import com.badoo.ribs.example.rib.menu.feature.MenuFeature
import com.uber.rib.core.Node
import com.uber.rib.core.ViewFactory
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object MenuModule {

    @MenuScope
    @Provides
    @JvmStatic
    internal fun router(): MenuRouter =
        MenuRouter()

    @MenuScope
    @Provides
    @JvmStatic
    fun feature(): MenuFeature =
        MenuFeature()

    @MenuScope
    @Provides
    @JvmStatic
    fun interactor(
        router: MenuRouter,
        input: ObservableSource<Menu.Input>,
        output: Consumer<Menu.Output>,
        feature: MenuFeature
    ): MenuInteractor =
        MenuInteractor(
            router = router,
            input = input,
            output = output,
            feature = feature
        )

    @MenuScope
    @Provides
    @JvmStatic
    internal fun node(
        viewFactory: ViewFactory<MenuView>,
        router: MenuRouter,
        interactor: MenuInteractor
    ) : Node<MenuView> = Node(
        forClass = Menu::class.java,
        viewFactory = viewFactory,
        router = router,
        interactor = interactor
    )
}
