package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Router

class MultiRoutingAction< V : RibView>(
    private vararg val routingActions: RoutingAction<V>
) : RoutingAction<V> {

    constructor(routingActions: List<RoutingAction<V>>) : this(*routingActions.toTypedArray())

    override fun onExecuteCreateTheseRibs(): List<() -> Router<*>> =
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
        fun < V : RibView> multi(vararg routingActions: RoutingAction<V>): RoutingAction<V> =
            MultiRoutingAction(*routingActions)
    }
}
