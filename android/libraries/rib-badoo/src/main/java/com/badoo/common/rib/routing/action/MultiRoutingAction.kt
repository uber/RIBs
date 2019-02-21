package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.uber.rib.core.RibAndroidView

class MultiRoutingAction< V : RibAndroidView>(
    private vararg val routingActions: RoutingAction<V>
) : RoutingAction<V> {

    constructor(routingActions: List<RoutingAction<V>>) : this(*routingActions.toTypedArray())

    override fun onExecuteCreateTheseRibs(): List<() -> BaseViewRouter<*>> =
        routingActions.flatMap {
            it.onExecuteCreateTheseRibs()
        }

    override fun onExecute() {
        routingActions.forEach {
            it.onExecute()
        }
    }

    override fun onLeave() {
        routingActions.forEach {
            it.onLeave()
        }
    }

    companion object {
        fun < V : RibAndroidView> multi(vararg routingActions: RoutingAction<V>): RoutingAction<V> =
            MultiRoutingAction(*routingActions)
    }
}
