package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RibDispatchersTest {

    @Test
    fun testInitialInstances() {
        assertThat(RibDispatchers.Default).isEqualTo(Dispatchers.Default)
        assertThat(RibDispatchers.IO).isEqualTo(Dispatchers.IO)
        assertThat(RibDispatchers.Main).isEqualTo(Dispatchers.Main.immediate)
        assertThat(RibDispatchers.Unconfined).isEqualTo(Dispatchers.Unconfined)
    }

    @Test
    fun testSetConfigDelegate() {
        val testDispatcher = DefaultRibDispatcherProvider()

        assertThat(RibDispatchers.Default).isNotEqualTo(testDispatcher.Default)
        assertThat(RibDispatchers.IO).isNotEqualTo(testDispatcher.IO)
        assertThat(RibDispatchers.Main).isNotEqualTo(testDispatcher.Main)
        assertThat(RibDispatchers.Unconfined).isNotEqualTo(testDispatcher.Unconfined)

        RibCoroutinesConfig.dispatchers = testDispatcher

        assertThat(RibDispatchers.Default).isEqualTo(testDispatcher.Default)
        assertThat(RibDispatchers.IO).isEqualTo(testDispatcher.IO)
        assertThat(RibDispatchers.Main).isEqualTo(testDispatcher.Main)
        assertThat(RibDispatchers.Unconfined).isEqualTo(testDispatcher.Unconfined)
    }

}