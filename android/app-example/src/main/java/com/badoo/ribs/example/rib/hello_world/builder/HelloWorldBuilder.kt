package com.badoo.ribs.example.rib.hello_world.builder

import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.HelloWorldView
import com.uber.rib.core.Builder
import com.uber.rib.core.Node

class HelloWorldBuilder(dependency: HelloWorld.Dependency) :
    Builder<HelloWorld.Dependency>(dependency) {

    fun build(): Node<HelloWorldView> {
        val customisation = dependency.ribCustomisation().get(HelloWorld.Customisation::class) ?: HelloWorld.Customisation()
        val component = DaggerHelloWorldComponent.builder()
            .dependency(dependency)
            .customisation(customisation)
            .build()

        return component.node()
    }
}
