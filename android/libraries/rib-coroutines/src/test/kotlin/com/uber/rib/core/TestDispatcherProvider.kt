package com.uber.rib.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
internal class TestDispatcherProvider : RibDispatchersProvider {
    override val Default = TestCoroutineDispatcher()
    override val Main = TestCoroutineDispatcher()
    override val IO = TestCoroutineDispatcher()
    override val Unconfined = TestCoroutineDispatcher()
}