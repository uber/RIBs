package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Router

class CompositeRoutingAction< V : RibView>(
    private vararg val routingActions: RoutingAction<V>
) : RoutingAction<V> {

    constructor(routingActions: List<RoutingAction<V>>) : this(*routingActions.toTypedArray())

    override fun ribFactories(): List<() -> Router<*>> =
        routingActions.flatMap {
            it.ribFactories()
        }

    override fun execute() {
        routingActions.forEach {
            it.execute()
        }
    }

    override fun cleanup() {
        routingActions.forEach {
            it.cleanup()
        }
    }

    companion object {
        fun < V : RibView> composite(vararg routingActions: RoutingAction<V>): RoutingAction<V> =
            CompositeRoutingAction(*routingActions)
    }
}
