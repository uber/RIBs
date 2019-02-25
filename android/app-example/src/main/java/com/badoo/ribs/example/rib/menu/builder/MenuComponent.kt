package com.badoo.ribs.example.rib.menu.builder

import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.MenuView
import com.badoo.ribs.core.Node

@MenuScope
@dagger.Component(
    modules = [MenuModule::class],
    dependencies = [
        Menu.Dependency::class,
        Menu.Customisation::class
    ]
)
interface MenuComponent {

    @dagger.Component.Builder
    interface Builder {

        fun dependency(component: Menu.Dependency): Builder

        fun customisation(customisation: Menu.Customisation): Builder

        fun build(): MenuComponent
    }

    fun node(): Node<MenuView>
}
