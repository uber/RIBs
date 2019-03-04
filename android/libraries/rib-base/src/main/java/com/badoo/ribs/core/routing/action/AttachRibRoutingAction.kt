package com.badoo.ribs.core.routing.action

import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.Node

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
