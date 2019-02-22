package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarNode
import com.uber.rib.core.Builder

class FooBarBuilder(dependency: FooBar.Dependency) :
    Builder<FooBarNode, FooBar.Dependency>(dependency) {

    fun build(): FooBarNode {
        val customisation = dependency.ribCustomisation().get(FooBar.Customisation::class) ?: FooBar.Customisation()
        val component = DaggerFooBarComponent.builder()
            .dependency(dependency)
            .customisation(customisation)
            .build()

        return component.router()
    }
}
