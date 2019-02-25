package com.badoo.ribs.example.rib.menu.builder

import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.MenuView
import com.uber.rib.core.Builder
import com.uber.rib.core.Node

class MenuBuilder(dependency: Menu.Dependency) :
    Builder<Menu.Dependency>(dependency) {

    fun build(): Node<MenuView> {
        val customisation = dependency.ribCustomisation().get(Menu.Customisation::class) ?: Menu.Customisation()
        val component = DaggerMenuComponent.builder()
            .dependency(dependency)
            .customisation(customisation)
            .build()

        return component.node()
    }
}
