package com.uber.rib.core.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.uber.rib.core.Router
import com.uber.rib.core.routing.action.RoutingAction
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class BackStackElement<C : Parcelable>(
    val configuration: C,
    var bundles: List<Bundle> = emptyList()
): Parcelable {
    @IgnoredOnParcel var routingAction: RoutingAction<*>? = null
    @IgnoredOnParcel var ribs: List<Router<*>>? = null
}
