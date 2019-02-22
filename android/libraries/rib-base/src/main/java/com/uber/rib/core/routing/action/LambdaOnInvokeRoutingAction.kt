package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView

class LambdaOnInvokeRoutingAction< V : RibView>(
    private val onInvoke: () -> Unit
) : RoutingAction<V> {

    override fun onExecute() {
        onInvoke()
    }

    companion object {
        fun < V : RibView> execute(onInvoke: () -> Unit): RoutingAction<V> =
            LambdaOnInvokeRoutingAction(onInvoke)
    }
}
