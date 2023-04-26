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
package com.uber.rib.root.loggedout;

import com.uber.rib.core.ViewRouter;

/** Adds and removes children of {@link LoggedOutBuilder.LoggedOutScope}. */
public class LoggedOutRouter extends ViewRouter<LoggedOutView, LoggedOutInteractor> {

  public LoggedOutRouter(
      LoggedOutView view, LoggedOutInteractor interactor, LoggedOutBuilder.Component component) {
    super(view, interactor, component);
  }
}
