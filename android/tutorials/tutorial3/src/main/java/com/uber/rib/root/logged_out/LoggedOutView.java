package com.uber.rib.root.logged_out;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.uber.rib.core.Initializer;
import com.uber.rib.tutorial1.R;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

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

/**
 * Top level view for {@link LoggedOutBuilder.LoggedOutScope}.
 */
public class LoggedOutView extends LinearLayout implements LoggedOutInteractor.LoggedOutPresenter {

  private Button loginButton;
  private EditText playerOneEditText;
  private EditText playerTwoEditText;

  public LoggedOutView(Context context) {
    this(context, null);
  }

  public LoggedOutView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoggedOutView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Initializer
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    playerOneEditText = (EditText) findViewById(R.id.player_one_name);
    playerTwoEditText = (EditText) findViewById(R.id.player_two_name);
    loginButton = (Button) findViewById(R.id.login_button);
  }

  @Override
  public Observable<Pair<String, String>> playerNames() {
    return RxView.clicks(findViewById(R.id.login_button))
        .map(
            new Function<Object, Pair<String, String>>() {
              @Override
              public Pair<String, String> apply(Object irrelevant) throws Exception {
                return new Pair<>(
                    playerOneEditText.getText().toString(), playerTwoEditText.getText().toString());
              }
            });
  }
}
