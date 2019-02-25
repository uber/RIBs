package com.badoo.ribs.example.rib.foo_bar

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.binder.using
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Router
import com.badoo.ribs.example.rib.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature
import com.badoo.ribs.example.rib.foo_bar.mapper.InputToWish
import com.badoo.ribs.example.rib.foo_bar.mapper.NewsToOutput
import com.badoo.ribs.example.rib.foo_bar.mapper.ViewEventToAnalyticsEvent
import com.badoo.ribs.example.rib.foo_bar.mapper.ViewEventToWish
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class FooBarInteractor(
    router: Router<FooBarRouter.Configuration, FooBarView>,
    private val input: ObservableSource<FooBar.Input>,
    private val output: Consumer<FooBar.Output>,
    private val feature: FooBarFeature
) : Interactor<FooBarRouter.Configuration, FooBarView>(
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

    override fun onViewCreated(view: FooBarView, viewLifecycle: Lifecycle) {
        super.onViewCreated(view, viewLifecycle)
        viewLifecycle.createDestroy {
            bind(view to FooBarAnalytics using ViewEventToAnalyticsEvent)
            bind(view to feature using ViewEventToWish)
        }
    }


}
