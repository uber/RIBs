package com.uber.rib.root.logged_out;

import static org.mockito.Matchers.any;
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
    String fakeName1 = "1";
    String fakeName2 = "2";
    when(presenter.playerNames()).thenReturn(Observable.just(new Pair<>(fakeName1, fakeName2)));

    InteractorHelper.attach(interactor, presenter, router, null);

    verify(listener).requestLogin(any(String.class), any(String.class));
  }

  @Test
  public void attach_whenViewEmitsEmptyName_shouldNotCallListener() {
    when(presenter.playerNames()).thenReturn(Observable.just(new Pair<String, String>("", "")));

    InteractorHelper.attach(interactor, presenter, router, null);

    verify(listener, never()).requestLogin(any(String.class), any(String.class));
  }
}
