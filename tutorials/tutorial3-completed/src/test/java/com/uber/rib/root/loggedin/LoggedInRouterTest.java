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
import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.root.loggedin.offgame.OffGameBuilder;
import com.uber.rib.root.loggedin.tictactoe.TicTacToeBuilder;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LoggedInRouterTest extends RibTestBasePlaceholder {

  @Mock LoggedInBuilder.Component component;
  @Mock LoggedInInteractor interactor;
  @Mock ViewGroup parentView;
  @Mock OffGameBuilder offGameBuilder;
  @Mock TicTacToeBuilder ticTacToeBuilder;

  private LoggedInRouter router;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    router =
        new LoggedInRouter(interactor, component, parentView, offGameBuilder, ticTacToeBuilder);
  }
}
