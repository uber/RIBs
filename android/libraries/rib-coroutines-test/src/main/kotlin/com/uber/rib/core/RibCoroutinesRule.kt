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

import kotlinx.coroutines.CoroutineDispatcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * RibCoroutinesRule is a Junit TestRule to act as a managed TestCoroutineScope in test and to
 * facilitate install and cleanup of Test Dispatchers
 */
public class RibCoroutinesRule(
  public val ribDispatchers: TestRibDispatchers = TestRibDispatchers()
) : TestWatcher() {

  private var originalDeprecatedWorkerDispatcher: CoroutineDispatcher? = null

  override fun starting(description: Description) {
    ribDispatchers.installTestDispatchers()
    originalDeprecatedWorkerDispatcher = RibCoroutinesConfig.deprecatedWorkerDispatcher
    RibCoroutinesConfig.deprecatedWorkerDispatcher = ribDispatchers.Unconfined
  }

  override fun finished(description: Description) {
    ribDispatchers.resetTestDispatchers()
    RibCoroutinesConfig.deprecatedWorkerDispatcher = originalDeprecatedWorkerDispatcher!!
  }
}
