package com.badoo.ribs.example.rib.switcher.mapper

import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.feature.SwitcherFeature

internal object NewsToOutput : (SwitcherFeature.News) -> Switcher.Output? {

    override fun invoke(news: SwitcherFeature.News): Switcher.Output? =
        TODO("Implement SwitcherNewsToOutput mapping")
}
