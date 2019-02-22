package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Router

class AttachRibPermanentlyRoutingAction< V : RibView>(
    private val builder: () -> Router<*>
) : RoutingAction<V> {

    private var router: Router<*>? = null

    override fun execute() {
        if (router == null) {
            builder.invoke().let {
                router = it
                // attachChildRouter(it)
            }
        }
    }

    override fun cleanup() {
        // no-op
    }

    companion object {
        fun < V : RibView> attachPermanent(builder: () -> Router<*>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
