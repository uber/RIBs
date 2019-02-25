package com.badoo.ribs.example.rib.foo_bar.builder

import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarView
import com.badoo.ribs.core.Node


@FooBarScope
@dagger.Component(
    modules = [FooBarModule::class],
    dependencies = [
        FooBar.Dependency::class,
        FooBar.Customisation::class
    ]
)
internal interface FooBarComponent {

    @dagger.Component.Builder
    interface Builder {

        fun dependency(component: FooBar.Dependency): Builder

        fun customisation(component: FooBar.Customisation): Builder

        fun build(): FooBarComponent
    }

    fun node(): Node<FooBarView>
}


