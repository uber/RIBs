package com.uber.rib.root.logged_out;

import com.uber.rib.core.RibTestBasePlaceholder;
import com.uber.rib.core.RouterHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NullAway")
public class LoggedOutRouterTest extends RibTestBasePlaceholder {

    @Mock LoggedOutBuilder.Component component;
    @Mock LoggedOutInteractor interactor;
    @Mock LoggedOutView view;

    private LoggedOutRouter router;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
