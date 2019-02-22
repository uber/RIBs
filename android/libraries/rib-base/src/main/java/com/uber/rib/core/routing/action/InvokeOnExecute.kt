package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView

class InvokeOnExecute< V : RibView>(
    private val onInvoke: () -> Unit
) : RoutingAction<V> {

    override fun execute() {
        onInvoke()
    }

    companion object {
        fun < V : RibView> execute(onInvoke: () -> Unit): RoutingAction<V> =
            InvokeOnExecute(onInvoke)
    }
}
