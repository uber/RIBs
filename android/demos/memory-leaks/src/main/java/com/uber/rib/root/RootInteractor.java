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
import com.uber.rib.core.Bundle;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.root.logged_out.LoggedOutInteractor;
import javax.inject.Inject;

/**
 * Coordinates Business Logic for {@link RootBuilder.RootScope}.
 */
@RibInteractor
public class RootInteractor extends Interactor<RootInteractor.RootPresenter, RootRouter> {

  @Inject RootPresenter presenter;

  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);
    getRouter().attachLoggedOut();
  }

  class LoggedOutListener implements LoggedOutInteractor.Listener {

    @Override
    public void login(String userName) {
      // Switch to logged in. Let’s just ignore userName for now.
      getRouter().detachLoggedOut();
      getRouter().attachLoggedIn();
    }
  }

  /**
   * Presenter interface implemented by this RIB's view.
   */
  interface RootPresenter {

  }
}
