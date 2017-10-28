package com.uber.rib.root.logged_in.tic_tac_toe;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.RouterHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TicTacToeRouterTest extends RibTestBasePlaceholder {

    @Mock TicTacToeBuilder.Component component;
    @Mock TicTacToeInteractor interactor;
    @Mock TicTacToeView view;

    private TicTacToeRouter router;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        router = new TicTacToeRouter(view, interactor, component);
    }

    /**
     * TODO: Delete this example and add real tests.
     */
    @Test
    public void anExampleTest_withSomeConditions_shouldPass() {
        // Use RouterHelper to drive your router's lifecycle.
        RouterHelper.attach(router);
        RouterHelper.detach(router);

        throw new RuntimeException("Remove this test and add real tests.");
    }

}
