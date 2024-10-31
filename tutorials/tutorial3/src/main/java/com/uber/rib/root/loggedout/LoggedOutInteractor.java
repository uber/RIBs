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

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import com.uber.rib.core.Bundle;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.root.loggedout.LoggedOutBuilder.LoggedOutScope;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import javax.inject.Inject;

/** Coordinates Business Logic for {@link LoggedOutScope}. */
@RibInteractor
public class LoggedOutInteractor
    extends Interactor<LoggedOutInteractor.LoggedOutPresenter, LoggedOutRouter> {

  @Inject Listener listener;
  @Inject LoggedOutPresenter presenter;

  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);
    presenter
        .loginName()
        .subscribe(
            new Consumer<Pair<String, String>>() {
              @Override
              public void accept(Pair<String, String> names) throws Exception {
                if (!isEmpty(names.first) && !isEmpty(names.second)) {
                  listener.login(names.first, names.second);
                }
              }
            });
  }

  private boolean isEmpty(@Nullable String string) {
    return string == null || string.length() == 0;
  }

  /** Presenter interface implemented by this RIB's view. */
  interface LoggedOutPresenter {

    Observable<Pair<String, String>> loginName();
  }

  public interface Listener {
    void login(String userNameA, String userNameB);
  }
}
