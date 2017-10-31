package com.uber.rib.root.logged_in.tic_tac_toe;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.InteractorHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TicTacToeInteractorTest extends RibTestBasePlaceholder {

    private final String playerOne = "playerOne";
    private final String playerTwo = "playerTwo";

    @Mock Board board;
    @Mock TicTacToeInteractor.Listener listener;
    @Mock TicTacToeInteractor.TicTacToePresenter presenter;
    @Mock TicTacToeRouter router;

    private TicTacToeInteractor interactor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        interactor = TestTicTacToeInteractor.create(
            board,
            listener,
            presenter,
            playerOne,
            playerTwo);
    }
}
