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

/** Class that provides its instance to emit or subscribe to [RouterNavigatorEvent]  */
class RouterNavigatorEvents private constructor() {

  private val events: PublishRelay<RouterNavigatorEvent> = PublishRelay.create()

  /** @return the stream which can be subcribed to listen for [RouterNavigatorEvent] */
  open fun getEvents(): Observable<RouterNavigatorEvent> {
    return events.hide()
  }

  /**
   * Emits a new [RouterNavigatorEvent] on the stream.
   *
   * @param eventType type of the navigation event.
   * @param parent router instance to which child will attach to.
   * @param child router instance which getting attached.
   */
  open fun emitEvent(eventType: RouterNavigatorEventType, parent: Router<*>, child: Router<*>) {
    events.accept(RouterNavigatorEvent(eventType, parent, child))
  }

  companion object {
    /** @return the singleton instance */
    @JvmStatic
    val instance = RouterNavigatorEvents()
  }
}
