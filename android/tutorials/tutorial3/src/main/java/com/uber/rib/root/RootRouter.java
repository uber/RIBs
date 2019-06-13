/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.rib.root;

import androidx.annotation.Nullable;

import com.uber.rib.core.ViewRouter;
import com.uber.rib.root.logged_in.LoggedInBuilder;
import com.uber.rib.root.logged_out.LoggedOutBuilder;
import com.uber.rib.root.logged_out.LoggedOutRouter;

/**
 * Adds and removes children of {@link RootBuilder.RootScope}.
 */
public class RootRouter extends ViewRouter<RootView, RootInteractor, RootBuilder.Component> {

  private final LoggedOutBuilder loggedOutBuilder;
  private final LoggedInBuilder loggedInBuilder;
  @Nullable private LoggedOutRouter loggedOutRouter;

  RootRouter(
      RootView view,
      RootInteractor interactor,
      RootBuilder.Component component,
      LoggedOutBuilder loggedOutBuilder,
      LoggedInBuilder loggedInBuilder) {
    super(view, interactor, component);
    this.loggedOutBuilder = loggedOutBuilder;
    this.loggedInBuilder = loggedInBuilder;
  }

  void attachLoggedOut() {
    loggedOutRouter = loggedOutBuilder.build(getView());
    attachChild(loggedOutRouter);
    getView().addView(loggedOutRouter.getView());
  }

  void detachLoggedOut() {
    if (loggedOutRouter != null) {
      detachChild(loggedOutRouter);
      getView().removeView(loggedOutRouter.getView());
      loggedOutRouter = null;
    }
  }

  void attachLoggedIn(String playerOne, String playerTwo) {
    attachChild(loggedInBuilder.build(playerOne, playerTwo));
  }
}
