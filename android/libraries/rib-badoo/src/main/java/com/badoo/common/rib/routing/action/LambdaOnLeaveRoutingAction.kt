package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.BaseViewRouterWithConfigurations
import com.uber.rib.core.RibView

class LambdaOnLeaveRoutingAction< V : RibView>(
    private val f: () -> Unit
) : RoutingAction<V> {

    override fun onLeave(removeChild: (BaseViewRouter<*, *>) -> Unit) {
        f()
    }

    companion object {
        fun < V : RibView> onLeave(onLeave: () -> Unit): RoutingAction<V> =
            LambdaOnLeaveRoutingAction(onLeave)
    }
}
