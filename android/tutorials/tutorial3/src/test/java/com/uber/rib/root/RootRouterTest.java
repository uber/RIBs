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
package com.uber.rib.root;

import com.uber.rib.root.loggedin.LoggedInBuilder;
import com.uber.rib.root.loggedout.LoggedOutBuilder;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RootRouterTest {

  @Mock RootBuilder.Component component;
  @Mock RootInteractor interactor;
  @Mock RootView view;

  private RootRouter router;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    router =
        new RootRouter(
            view,
            interactor,
            component,
            new LoggedOutBuilder(component),
            new LoggedInBuilder(component));
  }
}
