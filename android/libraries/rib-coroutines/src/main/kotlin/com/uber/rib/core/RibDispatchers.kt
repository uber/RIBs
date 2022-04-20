package com.uber.rib.core

import kotlinx.coroutines.Dispatchers

object RibDispatchers : RibDispatchersProvider by RibDispatchersConfig.delegate

object RibDispatchersConfig {
    var delegate = object : RibDispatchersProvider { }
}

interface RibDispatchersProvider {
    val Default
        get() = Dispatchers.Default

    val Main
        get() = Dispatchers.Main.immediate

    val IO
        get() = Dispatchers.IO

    val Unconfined
        get() = Dispatchers.Unconfined
}


