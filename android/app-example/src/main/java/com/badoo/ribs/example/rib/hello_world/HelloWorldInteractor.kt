package com.badoo.ribs.example.rib.hello_world

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.binder.using
import com.badoo.ribs.example.rib.hello_world.analytics.HelloWorldAnalytics
import com.badoo.ribs.example.rib.hello_world.feature.HelloWorldFeature
import com.badoo.ribs.example.rib.hello_world.mapper.InputToWish
import com.badoo.ribs.example.rib.hello_world.mapper.NewsToOutput
import com.badoo.ribs.example.rib.hello_world.mapper.ViewEventToAnalyticsEvent
import com.badoo.ribs.example.rib.hello_world.mapper.ViewEventToWish
import com.uber.rib.core.Interactor
import com.uber.rib.core.Node
import com.uber.rib.core.Router
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class HelloWorldInteractor(
    router: Router<HelloWorldRouter.Configuration, HelloWorldView>,
    private val input: ObservableSource<HelloWorld.Input>,
    private val output: Consumer<HelloWorld.Output>,
    private val feature: HelloWorldFeature
) : Interactor<HelloWorldRouter.Configuration, HelloWorldView>(
    router = router,
    disposables = feature
) {

    override fun didBecomeActive(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
        super.didBecomeActive(ribLifecycle, savedInstanceState)
        ribLifecycle.createDestroy {
            bind(feature.news to output using NewsToOutput)
            bind(input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: HelloWorldView, viewLifecycle: Lifecycle) {
        super.onViewCreated(view, viewLifecycle)
        viewLifecycle.createDestroy {
            bind(view to HelloWorldAnalytics using ViewEventToAnalyticsEvent)
            bind(view to feature using ViewEventToWish)
        }
    }


}
