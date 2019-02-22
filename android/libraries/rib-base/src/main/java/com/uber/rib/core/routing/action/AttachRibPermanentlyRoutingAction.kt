package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Router

class AttachRibPermanentlyRoutingAction< V : RibView>(
    private val builder: () -> Router<*>
) : RoutingAction<V> {

    private var router: Router<*>? = null

    override fun onExecute() {
        if (router == null) {
            builder.invoke().let {
                router = it
                // addChild(it)
            }
        }
    }

    override fun onLeave() {
        // no-op
    }

    companion object {
        fun < V : RibView> attachPermanent(builder: () -> Router<*>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
