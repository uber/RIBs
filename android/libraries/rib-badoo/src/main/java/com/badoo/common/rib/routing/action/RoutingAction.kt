package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.uber.rib.core.RibView

interface RoutingAction<V : RibView> {

    fun onExecuteCreateTheseRibs() : List<() -> BaseViewRouter<*, *>> =
        emptyList()

    fun onExecute() {

    }

    fun onLeave()  {
    }

    companion object {
        fun <V : RibView> noop(): RoutingAction<V> = object : RoutingAction<V> {}
    }
}


