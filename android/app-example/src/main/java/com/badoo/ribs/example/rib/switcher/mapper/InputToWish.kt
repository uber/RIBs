package com.badoo.ribs.example.rib.switcher.mapper

import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.feature.SwitcherFeature

internal object InputToWish : (Switcher.Input) -> SwitcherFeature.Wish? {

    override fun invoke(event: Switcher.Input): SwitcherFeature.Wish? =
        TODO("Implement SwitcherInputToWish mapping")
}
