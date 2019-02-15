package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.uber.rib.core.RibView

class AttachRibRoutingAction< V : RibView>(
    private val builder: () -> BaseViewRouter<*, *>
) : RoutingAction<V> {

    private var router: BaseViewRouter<*, *>? = null

    override fun onExecute(addChild: (BaseViewRouter<*, *>) -> Unit) {
        if (router == null) {
            builder.invoke().let {
                router = it
                addChild(it)
            }
        }
    }

    override fun onLeave(removeChild: (BaseViewRouter<*, *>) -> Unit) {
        router?.let {
            removeChild(it)
            router = null
        }
    }

    companion object {
        fun < V : RibView> attach(builder: () -> BaseViewRouter<*, *>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
