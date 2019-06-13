package com.uber.rib.root.logged_out;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
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

import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import com.uber.rib.memory_leak.R;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Top level view for {@link LoggedOutBuilder.LoggedOutScope}.
 */
public class LoggedOutView extends LinearLayout implements LoggedOutInteractor.LoggedOutPresenter {

  public LoggedOutView(Context context) {
    this(context, null);
  }

  public LoggedOutView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoggedOutView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public Observable<String> loginName() {
    return RxView.clicks(findViewById(R.id.login_button))
        .map(new Function<Object, String>() {
          @Override
          public String apply(Object o) throws Exception {
            TextView textView = (TextView) findViewById(R.id.edit_text);
            return textView.getText().toString();
          }
        });
  }
}
