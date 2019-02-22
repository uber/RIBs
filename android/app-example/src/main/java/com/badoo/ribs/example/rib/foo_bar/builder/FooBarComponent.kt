package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarRouter


@FooBarScope
@dagger.Component(
    modules = [FooBarModule::class],
    dependencies = [
        FooBar.Dependency::class,
        FooBar.Customisation::class
    ]
)
interface FooBarComponent {

    @dagger.Component.Builder
    interface Builder {

        fun dependency(component: FooBar.Dependency): Builder

        fun customisation(component: FooBar.Customisation): Builder

        fun build(): FooBarComponent
    }

    fun router(): FooBarRouter
}


