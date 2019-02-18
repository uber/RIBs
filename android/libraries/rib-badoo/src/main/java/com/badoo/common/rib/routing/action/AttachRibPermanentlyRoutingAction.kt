package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.BaseViewRouterWithConfigurations
import com.uber.rib.core.RibView

class AttachRibPermanentlyRoutingAction< V : RibView>(
    private val builder: () -> BaseViewRouter<*, *>
) : RoutingAction<V> {

    private var router: BaseViewRouter<*, *>? = null

    override fun onExecute(/* addChild: (BaseViewRouter<*, *>) -> Unit )*/) {
        if (router == null) {
            builder.invoke().let {
                router = it
                // addChild(it)
            }
        }
    }

    override fun onLeave(/* removeChild: (BaseViewRouter<*, *>) -> Unit */) {
        // no-op
    }

    companion object {
        fun < V : RibView> attachPermanent(builder: () -> BaseViewRouter<*, *>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
