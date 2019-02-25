package com.badoo.ribs.core.routing.action

import com.badoo.ribs.core.view.RibView

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
