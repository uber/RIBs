package com.badoo.ribs.example.rib.menu.mapper

import com.badoo.ribs.example.rib.menu.MenuView
import com.badoo.ribs.example.rib.menu.feature.MenuFeature

object StateToViewModel: (MenuFeature.State) -> MenuView.ViewModel {
    override fun invoke(state: MenuFeature.State): MenuView.ViewModel =
        MenuView.ViewModel(state.selected)
}
