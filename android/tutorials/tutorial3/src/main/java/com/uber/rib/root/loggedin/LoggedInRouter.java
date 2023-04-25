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
package com.uber.rib.root.loggedin;

import android.view.ViewGroup;
import com.uber.rib.core.Router;
import com.uber.rib.root.loggedin.offgame.OffGameBuilder;
import com.uber.rib.root.loggedin.offgame.OffGameRouter;
import com.uber.rib.root.loggedin.tictactoe.TicTacToeBuilder;
import com.uber.rib.root.loggedin.tictactoe.TicTacToeRouter;

/** Adds and removes children of {@link LoggedInBuilder.LoggedInScope}. */
public class LoggedInRouter extends Router<LoggedInInteractor> {

  private final ViewGroup parentView;
  private final OffGameBuilder offGameBuilder;
  private final TicTacToeBuilder ticTacToeBuilder;
  private OffGameRouter offGameRouter;
  private TicTacToeRouter ticTacToeRouter;

  LoggedInRouter(
      LoggedInInteractor interactor,
      LoggedInBuilder.Component component,
      ViewGroup parentView,
      OffGameBuilder offGameBuilder,
      TicTacToeBuilder ticTacToeBuilder) {
    super(interactor, component);
    this.parentView = parentView;
    this.offGameBuilder = offGameBuilder;
    this.ticTacToeBuilder = ticTacToeBuilder;
  }

  @Override
  protected void willDetach() {
    super.willDetach();
    detachOffGame();
    detachTicTacToe();
  }

  void attachOffGame() {
    offGameRouter = offGameBuilder.build(parentView);
    attachChild(offGameRouter);
    parentView.addView(offGameRouter.getView());
  }

  void detachOffGame() {
    if (offGameRouter != null) {
      detachChild(offGameRouter);
      parentView.removeView(offGameRouter.getView());
      offGameRouter = null;
    }
  }

  void attachTicTacToe() {
    ticTacToeRouter = ticTacToeBuilder.build(parentView);
    attachChild(ticTacToeRouter);
    parentView.addView(ticTacToeRouter.getView());
  }

  void detachTicTacToe() {
    if (ticTacToeRouter != null) {
      detachChild(ticTacToeRouter);
      parentView.removeView(ticTacToeRouter.getView());
      ticTacToeRouter = null;
    }
  }
}
