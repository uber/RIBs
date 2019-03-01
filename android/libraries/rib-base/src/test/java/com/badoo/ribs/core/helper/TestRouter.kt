package com.badoo.ribs.core.helper

import android.os.Parcelable
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.routing.action.RoutingAction
import kotlinx.android.parcel.Parcelize

class TestRouter(
    initialConfiguration: Configuration,
    override val permanentParts: List<() -> Node<*>>,
    private val routingActionForC1: RoutingAction<TestView>,
    private val routingActionForC2: RoutingAction<TestView>,
    private val routingActionForC3: RoutingAction<TestView>
) : Router<TestRouter.Configuration, TestView>(
    initialConfiguration = initialConfiguration
) {

    sealed class Configuration : Parcelable {
        @Parcelize object C1 : Configuration()
        @Parcelize object C2 : Configuration()
        @Parcelize object C3 : Configuration()
    }

    override fun resolveConfiguration(configuration: Configuration): RoutingAction<TestView> =
        when (configuration) {
            is Configuration.C1 -> routingActionForC1
            is Configuration.C2 -> routingActionForC2
            is Configuration.C3 -> routingActionForC3
        }
}
