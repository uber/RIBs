package com.uber.rib.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineExceptionHandler

object RibCoroutinesConfig {
    /**
     * Specify [RibDispatchersProvider] that provide default [CoroutineDispatcher]'s for Rib based scopes.
     * Defaults to standard [Dispatchers].
     * Useful in areas where injecting Dispatchers is not ideal, such as Test.
     */
    @JvmStatic
    var dispatchers = createDefaultDispatchers()

    /**
     * Specify [CoroutineExceptionHandler] to be used with Rib based scopes.
     * Defaults to throwing exception.
     * Useful for specifying additional information before passed to [Thread.UncaughtExceptionHandler].
     */
    @JvmStatic
    var exceptionHandler = createDefaultExceptionHandler()

    /**
     * Resets delegate to default [RibDispatchersProvider].
     * Should be called after tests
     */
    @JvmStatic
    fun reset() {
        this.dispatchers = createDefaultDispatchers()
        this.exceptionHandler = createDefaultExceptionHandler()
    }

    private fun createDefaultDispatchers() = DefaultRibDispatcherProvider()

    private fun createDefaultExceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        throw (throwable)
    }

}