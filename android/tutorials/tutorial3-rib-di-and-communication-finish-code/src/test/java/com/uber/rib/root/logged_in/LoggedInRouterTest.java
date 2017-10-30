package com.uber.rib.root.logged_in;

import android.view.ViewGroup;
import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.RouterHelper;

import com.uber.rib.root.logged_in.off_game.OffGameBuilder;
import com.uber.rib.root.logged_in.tic_tac_toe.TicTacToeBuilder;
import org.junit.Before;
import org.junit.Test;
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

        router = new LoggedInRouter(
            interactor,
            component,
            parentView,
            offGameBuilder,
            ticTacToeBuilder);
    }
}
