package com.badoo.ribs.example.rib.switcher.builder

import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.builder.FooBarBuilder
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.builder.HelloWorldBuilder
import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.builder.MenuBuilder
import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.SwitcherInteractor
import com.badoo.ribs.example.rib.switcher.SwitcherRouter
import com.badoo.ribs.example.rib.switcher.SwitcherView
import com.badoo.ribs.example.rib.switcher.feature.SwitcherFeature
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object SwitcherModule {

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: SwitcherComponent
    ): SwitcherRouter =
        SwitcherRouter(
            fooBarBuilder = FooBarBuilder(component),
            helloWorldBuilder = HelloWorldBuilder(component),
            menuBuilder = MenuBuilder(component)
        )

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun feature(): SwitcherFeature =
        SwitcherFeature()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun interactor(
        router: SwitcherRouter
    ): SwitcherInteractor =
        SwitcherInteractor(
            router = router
        )

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun node(
        viewFactory: ViewFactory<SwitcherView>,
        router: SwitcherRouter,
        interactor: SwitcherInteractor,
        activityStarter: ActivityStarter
    ) : Node<SwitcherView> = Node(
        identifier = object : Switcher {},
        viewFactory = viewFactory,
        router = router,
        interactor = interactor,
        activityStarter = activityStarter
    )

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun helloWorldInput(): ObservableSource<HelloWorld.Input> = Observable.empty()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun helloWorldOutput(): Consumer<HelloWorld.Output> = Consumer { }

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun fooBarInput(): ObservableSource<FooBar.Input> = Observable.empty()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun fooBarOutput(): Consumer<FooBar.Output> = Consumer { }

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun menuUpdater(
        router: SwitcherRouter
    ): ObservableSource<Menu.Input> =
        router.menuUpdater

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun menuListener(
        interactor: SwitcherInteractor
    ): Consumer<Menu.Output> =
        interactor.MenuListener()
}
