package com.badoo.ribs.example.rib.hello_world.mapper

import com.badoo.ribs.example.rib.hello_world.HelloWorldView
import com.badoo.ribs.example.rib.hello_world.analytics.HelloWorldAnalytics
import com.badoo.ribs.example.rib.hello_world.analytics.HelloWorldAnalytics.Event.ViewEvent

internal object ViewEventToAnalyticsEvent : (HelloWorldView.Event) -> HelloWorldAnalytics.Event? {

    override fun invoke(event: HelloWorldView.Event): HelloWorldAnalytics.Event? =
        ViewEvent(event)
}
