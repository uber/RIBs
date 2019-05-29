package com.uber.rib.root;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NullAway")
public class RootInteractorTest {

  @Mock RootInteractor.RootPresenter presenter;
  @Mock RootRouter router;

  private RootInteractor interactor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    interactor = TestRootInteractor.create(presenter);
  }
}
