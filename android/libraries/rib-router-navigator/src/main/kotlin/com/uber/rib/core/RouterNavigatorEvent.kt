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

/**
 * Event which is triggered when {@link StackRouterNavigator} is used to navigate between routers.
 */
public class RouterNavigatorEvent {
  private final RouterNavigatorEventType eventType;
  private final Router router;
  private final Router parentRouter;

  public RouterNavigatorEvent(
      RouterNavigatorEventType eventType, Router parentRouter, Router router) {
    this.eventType = eventType;
    this.parentRouter = parentRouter;
    this.router = router;
  }

  /**
   * @return the {@link RouterNavigatorEventType} for ex. {@link
   *     RouterNavigatorEventType#WILL_ATTACH_TO_HOST}
   */
  public RouterNavigatorEventType getEventType() {
    return eventType;
  }

  /** @return the instance of child {@link Router} */
  public Router getRouter() {
    return router;
  }

  /** @return the instance of Parent {@link Router} */
  public Router getParentRouter() {
    return parentRouter;
  }
}
