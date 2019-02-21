package com.badoo.common.rib.routing.action

import com.uber.rib.core.RibAndroidView

class LambdaOnLeaveRoutingAction< V : RibAndroidView>(
    private val f: () -> Unit
) : RoutingAction<V> {

    override fun onLeave() {
        f()
    }

    companion object {
        fun < V : RibAndroidView> onLeave(onLeave: () -> Unit): RoutingAction<V> =
            LambdaOnLeaveRoutingAction(onLeave)
    }
}
