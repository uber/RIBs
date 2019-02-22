package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarRouter
import com.uber.rib.core.Builder

class FooBarBuilder(dependency: FooBar.Dependency) :
    Builder<FooBarRouter, FooBar.Dependency>(dependency) {

    fun build(): FooBarRouter {
        val customisation = dependency.ribCustomisation().get(FooBar.Customisation::class) ?: FooBar.Customisation()
        val component = DaggerFooBarComponent.builder()
            .dependency(dependency)
            .customisation(customisation)
            .build()

        return component.router()
    }
}
