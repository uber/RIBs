package com.uber.rib.root.logged_in.off_game;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.RouterHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OffGameRouterTest extends RibTestBasePlaceholder {

    @Mock OffGameBuilder.Component component;
    @Mock OffGameInteractor interactor;
    @Mock OffGameView view;

    private OffGameRouter router;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        router = new OffGameRouter(view, interactor, component);
    }
}
