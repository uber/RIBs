package com.badoo.ribs.example.rib.switcher.mapper

import com.badoo.ribs.example.rib.switcher.SwitcherView
import com.badoo.ribs.example.rib.switcher.analytics.SwitcherAnalytics
import com.badoo.ribs.example.rib.switcher.analytics.SwitcherAnalytics.Event.ViewEvent

internal object ViewEventToAnalyticsEvent : (SwitcherView.Event) -> SwitcherAnalytics.Event? {

    override fun invoke(event: SwitcherView.Event): SwitcherAnalytics.Event? =
        ViewEvent(event)
}
