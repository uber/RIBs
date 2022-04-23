/*
 * Copyright (C) 2022. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RibDispatchersTest {

  @Test
  internal fun testInitialInstances() {
    assertThat(RibDispatchers.Default).isEqualTo(Dispatchers.Default)
    assertThat(RibDispatchers.IO).isEqualTo(Dispatchers.IO)
    assertThat(RibDispatchers.Main).isEqualTo(Dispatchers.Main)
    assertThat(RibDispatchers.Unconfined).isEqualTo(Dispatchers.Unconfined)
  }

  @Test
  internal fun testSetConfigDelegate() {
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
