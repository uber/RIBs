package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Node

class AttachRibRoutingAction< V : RibView>(
    private val builder: () -> Node<*>
) : RoutingAction<V> {

    override fun ribFactories(): List<() -> Node<*>> =
        listOf(builder)

    companion object {
        fun < V : RibView> attach(builder: () -> Node<*>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
