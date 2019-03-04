package com.badoo.ribs.example.rib.hello_world.mapper

import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.feature.HelloWorldFeature

internal object InputToWish : (HelloWorld.Input) -> HelloWorldFeature.Wish? {

    override fun invoke(event: HelloWorld.Input): HelloWorldFeature.Wish? =
        TODO("Implement HelloWorldInputToWish mapping")
}
