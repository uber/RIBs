package com.uber.rib.root.logged_in.off_game;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.InteractorHelper;

import com.uber.rib.root.logged_in.ScoreStream;
import org.junit.Before;
import org.junit.Test;
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

        interactor = TestOffGameInteractor.create(
            playerOne,
            playerTwo,
            listener,
            presenter,
            scoreStream);
    }
}
