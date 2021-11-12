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

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class RibEvents private constructor() {

  private val eventRelay: PublishRelay<RibEvent> = PublishRelay.create()

  open val events: Observable<RibEvent> = eventRelay.hide()

  /**
   * @param eventType [RibEventType]
   * @param child [Router]
   * @param parent [Router] and null for the root ribs that are directly attached to
   * RibActivity/Fragment
   */
  open fun emitEvent(eventType: RibEventType, child: Router<*>, parent: Router<*>?) {
    eventRelay.accept(RibEvent(eventType, child, parent))
  }

  companion object {
    private val instance: RibEvents = RibEvents()

    @JvmStatic
    fun getInstance() = instance
  }
}
