package com.badoo.ribs.example.rib.switcher.analytics

import com.badoo.ribs.example.rib.switcher.SwitcherView
import io.reactivex.functions.Consumer

internal object SwitcherAnalytics : Consumer<SwitcherAnalytics.Event> {

    sealed class Event {
        data class ViewEvent(val event: SwitcherView.Event) : Event()
    }

    override fun accept(event: SwitcherAnalytics.Event) {
        // TODO Implement tracking
    }
}
