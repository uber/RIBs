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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.core.util.Pair;
import com.uber.rib.core.InteractorHelper;
import com.uber.rib.core.RibTestBasePlaceholder;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LoggedOutInteractorTest extends RibTestBasePlaceholder {

  @Mock LoggedOutInteractor.Listener listener;
  @Mock LoggedOutInteractor.LoggedOutPresenter presenter;
  @Mock LoggedOutRouter router;

  private LoggedOutInteractor interactor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    interactor = TestLoggedOutInteractor.create(listener, presenter);
  }

  @Test
  public void attach_whenViewEmitsName_shouldCallListener() {
    when(presenter.loginName()).thenReturn(Observable.just(Pair.create("fakename", "fakename")));

    InteractorHelper.attach(interactor, presenter, router, null);

    verify(listener).login(any(String.class), any(String.class));
  }

  @Test
  public void attach_whenViewEmitsEmptyName_shouldNotCallListener() {
    when(presenter.loginName()).thenReturn(Observable.just(Pair.create("", "")));

    InteractorHelper.attach(interactor, presenter, router, null);

    verify(listener, never()).login(any(String.class), any(String.class));
  }
}
