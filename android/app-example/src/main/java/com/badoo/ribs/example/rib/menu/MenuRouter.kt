package com.badoo.ribs.example.rib.menu

import android.os.Parcelable
import com.uber.rib.core.Router
import com.uber.rib.core.routing.action.RoutingAction
import kotlinx.android.parcel.Parcelize

class MenuRouter : Router<MenuRouter.Configuration, MenuView>(
    initialConfiguration = Configuration.Default
) {
    sealed class Configuration : Parcelable {
        @Parcelize object Default : Configuration()
    }

    override fun resolveConfiguration(configuration: Configuration): RoutingAction<MenuView> =
        RoutingAction.noop()
}
