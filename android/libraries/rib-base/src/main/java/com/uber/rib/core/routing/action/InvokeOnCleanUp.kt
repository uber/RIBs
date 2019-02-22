package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView

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
