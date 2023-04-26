/*
 * Copyright (C) 2017. Uber Technologies
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

import io.reactivex.Observable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.rx2.asObservable

public class RibEvents private constructor() {

  private val _events = MutableSharedFlow<RibEvent>(0, 1, BufferOverflow.DROP_OLDEST)
  public val events: Observable<RibEvent> = _events.asObservable()

  /**
   * @param eventType [RibEventType]
   * @param child [Router]
   * @param parent [Router] and null for the root ribs that are directly attached to
   *   RibActivity/Fragment
   */
  public fun emitEvent(eventType: RibEventType, child: Router<*>, parent: Router<*>?) {
    _events.tryEmit(RibEvent(eventType, child, parent))
  }

  public companion object {
    private val instance: RibEvents = RibEvents()

    @JvmStatic public fun getInstance(): RibEvents = instance
  }
}
