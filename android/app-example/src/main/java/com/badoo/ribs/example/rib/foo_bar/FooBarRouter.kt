package com.badoo.ribs.example.rib.foo_bar

import android.os.Parcelable
import com.badoo.ribs.example.rib.foo_bar.FooBarRouter.Configuration
import com.uber.rib.core.Router
import com.uber.rib.core.routing.action.RoutingAction
import kotlinx.android.parcel.Parcelize

class FooBarRouter: Router<Configuration, FooBarView>(
    initialConfiguration = Configuration.Default
) {

    sealed class Configuration : Parcelable {
        @Parcelize object Default : Configuration()
    }

    override fun resolveConfiguration(configuration: Configuration): RoutingAction<FooBarView> =
        RoutingAction.noop()
}
