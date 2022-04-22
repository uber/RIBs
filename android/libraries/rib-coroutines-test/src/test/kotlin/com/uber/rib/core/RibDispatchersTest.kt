package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RibDispatchersTest {

    @Test
    fun testInitialInstances() {
        assertThat(RibDispatchers.Default).isEqualTo(Dispatchers.Default)
        assertThat(RibDispatchers.IO).isEqualTo(Dispatchers.IO)
        assertThat(RibDispatchers.Main).isEqualTo(Dispatchers.Main)
        assertThat(RibDispatchers.Unconfined).isEqualTo(Dispatchers.Unconfined)
    }

    @Test
    fun testSetConfigDelegate() {
        assertThat(RibDispatchers.Default).isNotInstanceOf(TestCoroutineDispatcher::class.java)
        assertThat(RibDispatchers.IO).isNotInstanceOf(TestCoroutineDispatcher::class.java)
        assertThat(RibDispatchers.Main).isNotInstanceOf(TestCoroutineDispatcher::class.java)
        assertThat(RibDispatchers.Unconfined).isNotInstanceOf(TestCoroutineDispatcher::class.java)

        val testDispatchers = TestRibDispatchers()
        testDispatchers.installTestDispatchers()

        assertThat(RibDispatchers.Default).isEqualTo(testDispatchers.Default)
        assertThat(RibDispatchers.IO).isEqualTo(testDispatchers.IO)
        assertThat(RibDispatchers.Main).isEqualTo(testDispatchers.Main)
        assertThat(RibDispatchers.Unconfined).isEqualTo(testDispatchers.Unconfined)

        testDispatchers.resetTestDispatchers()
    }


}