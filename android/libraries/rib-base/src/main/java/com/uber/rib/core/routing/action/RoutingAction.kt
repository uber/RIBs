package com.uber.rib.core.routing.action

import com.uber.rib.core.RibView
import com.uber.rib.core.Router

interface RoutingAction<V : RibView> {

    fun onExecuteCreateTheseRibs() : List<() -> Router<*>> =
        emptyList()

    fun onExecute() {

    }

    fun onLeave()  {
    }

    companion object {
        fun <V : RibView> noop(): RoutingAction<V> = object : RoutingAction<V> {}
    }
}


