package com.uber.rib.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object RibDispatchers : RibDispatchersProvider {
    override val Default get() = RibDispatchersConfig.delegate.Default
    override val Main get() = RibDispatchersConfig.delegate.Main
    override val IO get() = RibDispatchersConfig.delegate.IO
    override val Unconfined get() = RibDispatchersConfig.delegate.Unconfined
}

object RibDispatchersConfig {
    var delegate = object : RibDispatchersProvider { }
}

interface RibDispatchersProvider {
    val Default : CoroutineDispatcher
        get() = Dispatchers.Default

    val Main : CoroutineDispatcher
        get() = Dispatchers.Main.immediate

    val IO : CoroutineDispatcher
        get() = Dispatchers.IO

    val Unconfined : CoroutineDispatcher
        get() = Dispatchers.Unconfined
}


