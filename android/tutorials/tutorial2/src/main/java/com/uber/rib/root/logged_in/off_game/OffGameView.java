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

package com.uber.rib.root.logged_in.off_game;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.uber.rib.core.Initializer;
import com.uber.rib.tutorial1.R;

import io.reactivex.Observable;

/**
 * Top level view for {@link OffGameBuilder.OffGameScope}.
 */
public class OffGameView extends LinearLayout implements OffGameInteractor.OffGamePresenter {

  private Button button;

  public OffGameView(Context context) {
    this(context, null);
  }

  public OffGameView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public OffGameView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Initializer
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    button = (Button) findViewById(R.id.start_game_button);
  }

  @Override
  public Observable<Object> startGameRequest() {
    return RxView.clicks(button);
  }
}
