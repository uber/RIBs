package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.uber.rib.core.RibAndroidView

interface RoutingAction<V : RibAndroidView> {

    fun onExecuteCreateTheseRibs() : List<() -> BaseViewRouter<*>> =
        emptyList()

    fun onExecute() {

    }

    fun onLeave()  {
    }

    companion object {
        fun <V : RibAndroidView> noop(): RoutingAction<V> = object : RoutingAction<V> {}
    }
}


