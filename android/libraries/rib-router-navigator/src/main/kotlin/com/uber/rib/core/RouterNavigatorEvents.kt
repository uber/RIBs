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
package com.uber.rib.core;

import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Observable;

/** Class that provides its instance to emit or subscribe to {@link RouterNavigatorEvent} */
public final class RouterNavigatorEvents {

  private static final RouterNavigatorEvents INSTANCE = new RouterNavigatorEvents();

  private final PublishRelay<RouterNavigatorEvent> events;

  private RouterNavigatorEvents() {
    this.events = PublishRelay.create();
  }

  /** @return the singleton instance */
  public static RouterNavigatorEvents getInstance() {
    return INSTANCE;
  }

  /** @return the stream which can be subcribed to listen for {@link RouterNavigatorEvent} */
  public Observable<RouterNavigatorEvent> getEvents() {
    return events.hide();
  }

  /**
   * Emits a new {@link RouterNavigatorEvent} on the stream.
   *
   * @param eventType type of the navigation event.
   * @param parent router instance to which child will attach to.
   * @param child router instance which getting attached.
   */
  public void emitEvent(RouterNavigatorEventType eventType, Router parent, Router child) {
    events.accept(new RouterNavigatorEvent(eventType, parent, child));
  }
}
