package com.badoo.ribs.example.rib.menu.feature

import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.feature.MenuFeature.State
//import com.badoo.ribs.example.rib.menu.feature.MenuFeature.Wish
//import com.badoo.ribs.example.rib.menu.feature.MenuFeature.Wish.*
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ReducerFeature

class MenuFeature : ReducerFeature<Menu.Input, State, Nothing>(
    initialState = State(),
    reducer = ReducerImpl()
) {

    data class State(
        val selected: Menu.MenuItem? = null
    )

    class ReducerImpl : Reducer<State, Menu.Input> {
        override fun invoke(state: State, wish: Menu.Input): State = when (wish) {
            is Menu.Input.SelectMenuItem -> state.copy(
                selected = wish.menuItem
            )
        }
    }
}
