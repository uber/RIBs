package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.uber.rib.core.RibView

class MultiRoutingAction< V : RibView>(
    private vararg val routingActions: RoutingAction<V>
) : RoutingAction<V> {

    constructor(routingActions: List<RoutingAction<V>>) : this(*routingActions.toTypedArray())

    override fun onExecute(addChild: (BaseViewRouter<*, *>) -> Unit) {
        routingActions.forEach {
            it.onExecute(addChild)
        }
    }

    override fun onLeave(removeChild: (BaseViewRouter<*, *>) -> Unit) {
        routingActions.forEach {
            it.onLeave(removeChild)
        }
    }

    companion object {
        fun < V : RibView> multi(vararg routingActions: RoutingAction<V>): RoutingAction<V> =
            MultiRoutingAction(*routingActions)
    }
}
