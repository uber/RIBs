package com.badoo.ribs.example.rib.hello_world.mapper

import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.feature.HelloWorldFeature

internal object NewsToOutput : (HelloWorldFeature.News) -> HelloWorld.Output? {

    override fun invoke(news: HelloWorldFeature.News): HelloWorld.Output? =
        TODO("Implement HelloWorldNewsToOutput mapping")
}
