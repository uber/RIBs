package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Router

class AttachRibRoutingAction< V : RibView>(
    private val builder: () -> Router<*>
) : RoutingAction<V> {

    override fun onExecuteCreateTheseRibs(): List<() -> Router<*>> =
        listOf(builder)

    companion object {
        fun < V : RibView> attach(builder: () -> Router<*>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
