package com.badoo.ribs.example.rib.foo_bar.mapper

import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature

internal object NewsToOutput : (FooBarFeature.News) -> FooBar.Output? {

    override fun invoke(news: FooBarFeature.News): FooBar.Output? =
        TODO("Implement FooBarNewsToOutput mapping")
}
