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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * RibCoroutinesRule is a Junit TestRule to act as a managed TestCoroutineScope in test and to facilitate install and cleanup of Test Dispatchers
 */
@ExperimentalCoroutinesApi
public class RibCoroutinesRule(public val ribDispatchers: TestRibDispatchers = TestRibDispatchers()) :
  TestWatcher(),
  TestCoroutineScope by TestCoroutineScope(ribDispatchers.Default) {

  override fun starting(description: Description) {
    ribDispatchers.installTestDispatchers()
  }

  override fun finished(description: Description) {
    cleanupTestCoroutines()
    ribDispatchers.cleanupTestDispatchers()
    ribDispatchers.resetTestDispatchers()
  }
}
