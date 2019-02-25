package com.badoo.ribs.example.rib.hello_world

import android.os.Parcelable
import com.badoo.ribs.example.rib.hello_world.HelloWorldRouter.Configuration
import com.uber.rib.core.Node
import com.uber.rib.core.Router
import com.uber.rib.core.routing.action.RoutingAction
import kotlinx.android.parcel.Parcelize

class HelloWorldRouter: Router<Configuration, HelloWorldView>(
    initialConfiguration = Configuration.Default
) {
    override val permanentParts: List<() -> Node<*>> =
        emptyList()

    sealed class Configuration : Parcelable {
        @Parcelize object Default : Configuration()
    }

    override fun resolveConfiguration(configuration: Configuration): RoutingAction<HelloWorldView> =
        RoutingAction.noop()
}
