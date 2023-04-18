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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
public data class TestRibDispatchers(
  /**
   * [TestCoroutineScheduler] to be used by all other [TestDispatcher] when using the default
   * constructor.
   *
   * Note that when passing in custom dispatchers, this test scheduler will not be used.
   */
  private val testScheduler: TestCoroutineScheduler? = TestCoroutineScheduler(),
  override val Default: TestDispatcher = StandardTestDispatcher(testScheduler),
  override val IO: TestDispatcher = StandardTestDispatcher(testScheduler),
  override val Unconfined: TestDispatcher = UnconfinedTestDispatcher(testScheduler),
  val MainTestDelegate: TestDispatcher = StandardTestDispatcher(testScheduler),
) : RibDispatchersProvider {

  public fun installTestDispatchers() {
    // MainTestCoroutineDispatcher is Internal, so we need to wrap it through the main API
    Dispatchers.setMain(MainTestDelegate)
    RibCoroutinesConfig.dispatchers = this
  }

  public fun resetTestDispatchers() {
    Dispatchers.resetMain()
    RibCoroutinesConfig.dispatchers = DefaultRibDispatchers()
  }

  override val Main: MainCoroutineDispatcher
    get() = Dispatchers.Main
}
