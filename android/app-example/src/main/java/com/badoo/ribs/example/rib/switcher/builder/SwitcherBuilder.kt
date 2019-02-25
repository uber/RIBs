package com.badoo.ribs.example.rib.switcher.builder

import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.SwitcherView
import com.uber.rib.core.Builder
import com.uber.rib.core.Node

class SwitcherBuilder(dependency: Switcher.Dependency) :
    Builder<Switcher.Dependency>(dependency) {

    fun build(): Node<SwitcherView> {
        val customisation = dependency.ribCustomisation().get(Switcher.Customisation::class) ?: Switcher.Customisation()
        val component = DaggerSwitcherComponent.builder()
            .dependency(dependency)
            .customisation(customisation)
            .build()

        return component.node()
    }
}
