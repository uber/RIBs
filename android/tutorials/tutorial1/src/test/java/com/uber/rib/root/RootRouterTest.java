package com.uber.rib.root;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NullAway")
public class RootRouterTest {

  @Mock RootBuilder.Component component;
  @Mock RootInteractor interactor;
  @Mock RootView view;

  private RootRouter router;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    router = new RootRouter(view, interactor, component);
  }
}
