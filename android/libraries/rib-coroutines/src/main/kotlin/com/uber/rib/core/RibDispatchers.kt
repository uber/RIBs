package com.uber.rib.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object RibDispatchers {

    var delegate : DispatcherProvider = DefaultDispatcherProvider()

    var Default = delegate.getDefault()
        private set

    var Main = delegate.getMain()
        private set

    var IO = delegate.getIO()
        private set

    var Unconfined = delegate.getUnconfined()
        private set

}

class DefaultDispatcherProvider : DispatcherProvider {
    override fun getDefault() = Dispatchers.Default

    override fun getMain() = Dispatchers.Main

    override fun getIO() = Dispatchers.IO

    override fun getUnconfined() = Dispatchers.Unconfined
}

interface DispatcherProvider {
    fun getDefault() : CoroutineDispatcher

    fun getMain() : CoroutineDispatcher

    fun getIO() : CoroutineDispatcher

    fun getUnconfined() : CoroutineDispatcher
}

