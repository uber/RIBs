package com.badoo.ribs.template.rib_with_view.foo_bar

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.binder.using
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Router
import com.badoo.ribs.template.rib_with_view.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.template.rib_with_view.foo_bar.feature.FooBarFeature
import com.badoo.ribs.template.rib_with_view.foo_bar.mapper.InputToWish
import com.badoo.ribs.template.rib_with_view.foo_bar.mapper.NewsToOutput
import com.badoo.ribs.template.rib_with_view.foo_bar.mapper.ViewEventToAnalyticsEvent
import com.badoo.ribs.template.rib_with_view.foo_bar.mapper.ViewEventToWish
import com.badoo.ribs.template.rib_with_view.foo_bar.FooBarRouter.Configuration
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class FooBarInteractor(
    router: Router<Configuration, FooBarView>,
    private val input: ObservableSource<FooBar.Input>,
    private val output: Consumer<FooBar.Output>,
    private val feature: FooBarFeature
) : Interactor<Configuration, FooBarView>(
    router = router,
    disposables = feature
) {

    override fun onAttach(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
        ribLifecycle.createDestroy {
            bind(feature.news to output using NewsToOutput)
            bind(input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: FooBarView, viewLifecycle: Lifecycle) {
        viewLifecycle.createDestroy {
            bind(view to FooBarAnalytics using ViewEventToAnalyticsEvent)
            bind(view to feature using ViewEventToWish)
        }
    }
}
