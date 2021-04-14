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

import androidx.annotation.Nullable;

public class RibEvent {

  private final RibEventType eventType;
  private final Router router;
  /** Null for the root ribs that are directly attached to RibActivity/Fragment */
  @Nullable private final Router parentRouter;

  public RibEventType getEventType() {
    return eventType;
  }

  public Router getRouter() {
    return router;
  }

  /** @return null for the root ribs that are directly attached to RibActivity/Fragment */
  @Nullable
  public Router getParentRouter() {
    return parentRouter;
  }

  /**
   * @param eventType {@link RibEventType}
   * @param router {@link Router}
   * @param parentRouter {@link Router} and null for the root ribs that are directly attached to
   *     RibActivity/Fragment
   */
  public RibEvent(RibEventType eventType, Router router, @Nullable Router parentRouter) {
    this.eventType = eventType;
    this.router = router;
    this.parentRouter = parentRouter;
  }
}
