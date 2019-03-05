package com.badoo.ribs.template.rib_with_view.foo_bar.mapper

import com.badoo.ribs.template.rib_with_view.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.template.rib_with_view.foo_bar.analytics.FooBarAnalytics.Event.ViewEvent
import com.badoo.ribs.template.rib_with_view.foo_bar.FooBarView.Event

internal object ViewEventToAnalyticsEvent : (Event) -> FooBarAnalytics.Event? {

    override fun invoke(event: Event): FooBarAnalytics.Event? =
        ViewEvent(event)
}
