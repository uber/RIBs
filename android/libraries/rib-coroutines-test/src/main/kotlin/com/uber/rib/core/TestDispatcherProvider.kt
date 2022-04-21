package com.uber.rib.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
internal class TestRibDispatcherProvider(override val Default : CoroutineDispatcher,
                                         override val Main: MainCoroutineDispatcher,
                                         override val IO : CoroutineDispatcher,
                                         override val Unconfined : CoroutineDispatcher) : RibDispatchersProvider