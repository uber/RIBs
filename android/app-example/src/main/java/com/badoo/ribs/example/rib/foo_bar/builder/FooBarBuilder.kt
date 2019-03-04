package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarView
import com.badoo.ribs.core.Builder
import com.badoo.ribs.core.Node

class FooBarBuilder(dependency: FooBar.Dependency) :
    Builder<FooBar.Dependency>(dependency) {

    fun build(): Node<FooBarView> {
        val customisation = dependency.ribCustomisation().get(FooBar.Customisation::class) ?: FooBar.Customisation()
        val component = DaggerFooBarComponent.builder()
            .dependency(dependency)
            .customisation(customisation)
            .build()

        return component.node()
    }
}
