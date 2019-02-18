package com.badoo.common.rib.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.routing.action.RoutingAction
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class BackStackElement<C : Parcelable>(
    val configuration: C,
    var bundles: List<Bundle> = emptyList()
): Parcelable {
    @IgnoredOnParcel var routingAction: RoutingAction<*>? = null
    @IgnoredOnParcel var ribs: List<BaseViewRouter<*, *>>? = null
}
