package com.uber.rib.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
data class TestRibDispatchers(override val Default: TestCoroutineDispatcher = TestCoroutineDispatcher(),
                              override val IO: TestCoroutineDispatcher = TestCoroutineDispatcher(),
                              override val Unconfined: TestCoroutineDispatcher = TestCoroutineDispatcher(),
                              val MainTestDelegate : TestCoroutineDispatcher = TestCoroutineDispatcher()) : RibDispatchersProvider {

    fun installTestDispatchers() {
        //MainTestCoroutineDispatcher is Internal, so we need to wrap it through the main API
        Dispatchers.setMain(MainTestDelegate)
        RibCoroutinesConfig.dispatchers = this
    }

    fun cleanupTestDispatchers() {
        Default.cleanupTestCoroutines()
        MainTestDelegate.cleanupTestCoroutines()
        IO.cleanupTestCoroutines()
        Unconfined.cleanupTestCoroutines()
    }

    fun resetTestDispatchers() {
        Dispatchers.resetMain()
        RibCoroutinesConfig.dispatchers = DefaultRibDispatchers()
    }

    override val Main: MainCoroutineDispatcher
        get() = Dispatchers.Main
}