package com.badoo.ribs.example.rib.hello_world.builder

import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.HelloWorld.Input
import com.badoo.ribs.example.rib.hello_world.HelloWorld.Output
import com.badoo.ribs.example.rib.hello_world.HelloWorldInteractor
import com.badoo.ribs.example.rib.hello_world.HelloWorldRouter
import com.badoo.ribs.example.rib.hello_world.HelloWorldView
import com.badoo.ribs.example.rib.hello_world.feature.HelloWorldFeature
import com.badoo.ribs.android.IntentCreator
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object HelloWorldModule {

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: HelloWorldComponent
    ): HelloWorldRouter =
        HelloWorldRouter()

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun feature(): HelloWorldFeature =
        HelloWorldFeature()

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun interactor(
        router: HelloWorldRouter,
        input: ObservableSource<Input>,
        output: Consumer<Output>,
        feature: HelloWorldFeature,
        intentCreator: IntentCreator
    ): HelloWorldInteractor =
        HelloWorldInteractor(
            router = router,
            input = input,
            output = output,
            feature = feature,
            intentCreator = intentCreator
        )

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun node(
        viewFactory: ViewFactory<HelloWorldView>,
        router: HelloWorldRouter,
        interactor: HelloWorldInteractor,
        activityStarter: ActivityStarter
    ) : Node<HelloWorldView> = Node(
        identifier = object : HelloWorld {},
        viewFactory = viewFactory,
        router = router,
        interactor = interactor,
        activityStarter = activityStarter
    )
}
