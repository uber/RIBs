package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.BaseViewRouterWithConfigurations
import com.uber.rib.core.RibView

class LambdaOnInvokeRoutingAction< V : RibView>(
    private val onInvoke: () -> Unit
) : RoutingAction<V> {

    override fun onExecute(addChild: (BaseViewRouter<*, *>) -> Unit) {
        onInvoke()
    }

    companion object {
        fun < V : RibView> execute(onInvoke: () -> Unit): RoutingAction<V> =
            LambdaOnInvokeRoutingAction(onInvoke)
    }
}
