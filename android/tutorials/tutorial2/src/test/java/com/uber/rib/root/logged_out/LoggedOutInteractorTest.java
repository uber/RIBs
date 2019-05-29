package com.uber.rib.root.logged_out;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.InteractorHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NullAway")
public class LoggedOutInteractorTest extends RibTestBasePlaceholder {

    @Mock LoggedOutInteractor.LoggedOutPresenter presenter;
    @Mock LoggedOutRouter router;

    private LoggedOutInteractor interactor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        interactor = TestLoggedOutInteractor.create(presenter);
    }

    /**
     * TODO: Delete this example and add real tests.
     */
    @Test
    public void anExampleTest_withSomeConditions_shouldPass() { }

}
