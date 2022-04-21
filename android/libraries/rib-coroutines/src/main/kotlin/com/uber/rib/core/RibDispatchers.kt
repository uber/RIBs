package com.uber.rib.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

object RibDispatchers : RibDispatchersProvider {
    override val Default get() = RibCoroutinesConfig.dispatchers.Default
    override val Main get() = RibCoroutinesConfig.dispatchers.Main
    override val IO get() = RibCoroutinesConfig.dispatchers.IO
    override val Unconfined get() = RibCoroutinesConfig.dispatchers.Unconfined
}

class DefaultRibDispatcherProvider(override val Default : CoroutineDispatcher = Dispatchers.Default,
                                         override val Main: MainCoroutineDispatcher = Dispatchers.Main,
                                         override val IO : CoroutineDispatcher = Dispatchers.IO,
                                         override val Unconfined : CoroutineDispatcher = Dispatchers.Unconfined) : RibDispatchersProvider

/**
 * Allows providing default Dispatchers used for Rib CoroutineScopes
 */
interface RibDispatchersProvider {
    /**
     * The Default [CoroutineDispatcher] that behaves as [Dispatchers.Default].
     *
     * The default CoroutineDispatcher that is used by all standard builders like launch, async, etc if no dispatcher nor any other ContinuationInterceptor is specified in their context.
     */
    val Default : CoroutineDispatcher
    /**
     * The Main [CoroutineDispatcher] that behaves as [Dispatchers.Main].
     *
     * A coroutine dispatcher that is confined to the Main thread operating with UI objects. This dispatcher can be used either directly or via MainScope factory. Usually such dispatcher is single-threaded.
     */
    val Main : MainCoroutineDispatcher
    /**
     * The IO [CoroutineDispatcher] that behaves as [Dispatchers.IO]
     *
     * The CoroutineDispatcher that is designed for offloading blocking IO tasks to a shared pool of threads.
     */
    val IO : CoroutineDispatcher

    /**
     * The Unconfined [CoroutineDispatcher] that behaves as [Dispatchers.Unconfined]
     *
     * A coroutine dispatcher that is not confined to any specific thread. It executes initial continuation of the coroutine in the current call-frame and lets the coroutine resume in whatever thread that is used by the corresponding suspending function, without mandating any specific threading policy. Nested coroutines launched in this dispatcher form an event-loop to avoid stack overflows.
     */
    val Unconfined : CoroutineDispatcher
}


