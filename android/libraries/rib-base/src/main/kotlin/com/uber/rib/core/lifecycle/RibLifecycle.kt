/*
 * Copyright (C) 2023. Uber Technologies
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
package com.uber.rib.core.lifecycle

import com.uber.rib.core.InternalRibsApi
import kotlinx.coroutines.flow.SharedFlow

/** An owner of a [RibLifecycle]. */
public interface RibLifecycleOwner<T : Comparable<T>> {
  public val ribLifecycle: RibLifecycle<T>

  // For retro-compatibility

  @InternalRibsApi
  @Deprecated("This field should never be used on real code", level = DeprecationLevel.ERROR)
  public val actualRibLifecycle: RibLifecycle<T>

  @Deprecated(
    message =
      """Use 'RibLifecycle.lifecycleFlow'. When mocking in tests, mock 'ribLifecycle' and return
         an instance of 'TestRibLifecycle' from the 'rib-test' module.""",
    replaceWith = ReplaceWith("ribLifecycle.lifecycleFlow"),
  )
  public val lifecycleFlow: SharedFlow<T>
    get() = ribLifecycle.lifecycleFlow
}

/** A RIB component that has a lifecycle. */
public interface RibLifecycle<T : Comparable<T>> {
  /** A flow of lifecycle events. */
  public val lifecycleFlow: SharedFlow<T>

  /** The range at which the lifecycle is considered active. */
  public val lifecycleRange: ClosedRange<T>
}
