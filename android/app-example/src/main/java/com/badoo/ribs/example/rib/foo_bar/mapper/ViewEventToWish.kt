package com.badoo.ribs.example.rib.foo_bar.mapper

import com.badoo.ribs.example.rib.foo_bar.FooBarView
import com.badoo.ribs.example.rib.foo_bar.feature.FooBarFeature

internal object ViewEventToWish : (FooBarView.Event) -> FooBarFeature.Wish? {

    override fun invoke(event: FooBarView.Event): FooBarFeature.Wish? =
        TODO("Implement FooBarViewEventToWish mapping")
}
