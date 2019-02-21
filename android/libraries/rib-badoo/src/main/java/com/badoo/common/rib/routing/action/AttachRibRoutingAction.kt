package com.badoo.common.rib.routing.action

import com.badoo.common.rib.BaseViewRouter
import com.uber.rib.core.RibAndroidView

class AttachRibRoutingAction< V : RibAndroidView>(
    private val builder: () -> BaseViewRouter<*>
) : RoutingAction<V> {

    override fun onExecuteCreateTheseRibs(): List<() -> BaseViewRouter<*>> =
        listOf(builder)

    companion object {
        fun < V : RibAndroidView> attach(builder: () -> BaseViewRouter<*>): RoutingAction<V> =
            AttachRibRoutingAction(builder)
    }
}
