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
package com.uber.rib.root.loggedin.offgame;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.root.loggedin.ScoreStream;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OffGameInteractorTest extends RibTestBasePlaceholder {

  private final String playerOne = "playerOne";
  private final String playerTwo = "playerTwo";

  @Mock OffGameInteractor.Listener listener;
  @Mock OffGameInteractor.OffGamePresenter presenter;
  @Mock OffGameRouter router;
  @Mock ScoreStream scoreStream;

  private OffGameInteractor interactor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    interactor =
        TestOffGameInteractor.create(playerOne, playerTwo, listener, presenter, scoreStream);
  }
}
