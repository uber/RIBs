package com.uber.rib.root.logged_in.off_game;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.InteractorHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OffGameInteractorTest extends RibTestBasePlaceholder {

    @Mock OffGameInteractor.Listener listener;
    @Mock OffGameInteractor.OffGamePresenter presenter;
    @Mock OffGameRouter router;

    private OffGameInteractor interactor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        interactor = TestOffGameInteractor.create(listener, presenter);
    }
}
