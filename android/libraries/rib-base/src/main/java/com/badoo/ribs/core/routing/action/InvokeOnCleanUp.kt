package com.badoo.ribs.core.routing.action

import com.badoo.ribs.core.view.RibView

class InvokeOnCleanup< V : RibView>(
    private val f: () -> Unit
) : RoutingAction<V> {

    override fun cleanup() {
        f()
    }

    companion object {
        fun < V : RibView> cleanup(onLeave: () -> Unit): RoutingAction<V> =
            InvokeOnCleanup(onLeave)
    }
}
