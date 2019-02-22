package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView

class LambdaOnLeaveRoutingAction< V : RibView>(
    private val f: () -> Unit
) : RoutingAction<V> {

    override fun onLeave() {
        f()
    }

    companion object {
        fun < V : RibView> onLeave(onLeave: () -> Unit): RoutingAction<V> =
            LambdaOnLeaveRoutingAction(onLeave)
    }
}
