package com.uber.rib.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RibDispatchersRule : TestWatcher() {

    override fun starting(description: Description) {
        val defaultDispatcher = TestCoroutineDispatcher()
        val mainDispatcher = TestCoroutineDispatcher()
        val ioDispatcher = TestCoroutineDispatcher()
        val unconfinedDispatcher = TestCoroutineDispatcher()

        Dispatchers.setMain(mainDispatcher)

        val mainDispatcherProxy = Dispatchers.Main

        RibCoroutinesConfig.dispatchers = DefaultRibDispatcherProvider(
                Default = defaultDispatcher,
                Main = mainDispatcherProxy,
                IO = ioDispatcher,
                Unconfined = unconfinedDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
        RibCoroutinesConfig.reset()
    }
}