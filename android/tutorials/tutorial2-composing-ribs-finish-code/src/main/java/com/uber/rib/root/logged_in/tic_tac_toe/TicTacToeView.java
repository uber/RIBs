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

package com.uber.rib.root.logged_in.tic_tac_toe;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;

/**
 * Top level view for {@link TicTacToeBuilder.TicTacToeScope}.
 */
class TicTacToeView extends PercentRelativeLayout implements
    TicTacToeInteractor.TicTacToePresenter {

  public TicTacToeView(Context context) {
    this(context, null);
  }

  public TicTacToeView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TicTacToeView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
