package com.badoo.ribs.example.rib.foo_bar

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.badoo.mvicore.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.uber.rib.core.RibInteractor
import com.badoo.ribs.example.rib.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature
import com.badoo.ribs.example.rib.foo_bar.mapper.InputToWish
import com.badoo.ribs.example.rib.foo_bar.mapper.NewsToOutput
import com.badoo.ribs.example.rib.foo_bar.mapper.ViewEventToWish
import com.badoo.ribs.example.rib.foo_bar.mapper.ViewEventToAnalyticsEvent
import com.uber.rib.core.Interactor
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@RibInteractor
class FooBarInteractor(
    private val input: ObservableSource<FooBar.Input>,
    private val output: Consumer<FooBar.Output>,
    private val feature: FooBarFeature
) : Interactor<FooBarView, FooBarRouter>(
    disposables = listOf(feature)
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
