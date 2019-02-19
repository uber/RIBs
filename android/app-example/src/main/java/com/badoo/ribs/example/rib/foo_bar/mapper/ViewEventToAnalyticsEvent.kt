package com.badoo.ribs.example.rib.foo_bar.mapper

import com.badoo.ribs.example.rib.foo_bar.FooBarView
import com.badoo.ribs.example.rib.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.example.rib.foo_bar.analytics.FooBarAnalytics.Event.ViewEvent

internal object ViewEventToAnalyticsEvent : (FooBarView.Event) -> FooBarAnalytics.Event? {

    override fun invoke(event: FooBarView.Event): FooBarAnalytics.Event? =
        ViewEvent(event)
}
