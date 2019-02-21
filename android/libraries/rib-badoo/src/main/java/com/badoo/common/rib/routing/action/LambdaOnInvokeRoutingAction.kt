package com.badoo.common.rib.routing.action

import com.uber.rib.core.RibAndroidView

class LambdaOnInvokeRoutingAction< V : RibAndroidView>(
    private val onInvoke: () -> Unit
) : RoutingAction<V> {

    override fun onExecute() {
        onInvoke()
    }

    companion object {
        fun < V : RibAndroidView> execute(onInvoke: () -> Unit): RoutingAction<V> =
            LambdaOnInvokeRoutingAction(onInvoke)
    }
}
