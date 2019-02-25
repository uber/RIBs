package com.badoo.ribs.example.rib.switcher.mapper

import com.badoo.ribs.example.rib.switcher.SwitcherView
import com.badoo.ribs.example.rib.switcher.feature.SwitcherFeature

internal object ViewEventToWish : (SwitcherView.Event) -> SwitcherFeature.Wish? {

    override fun invoke(event: SwitcherView.Event): SwitcherFeature.Wish? =
        TODO("Implement SwitcherViewEventToWish mapping")
}
