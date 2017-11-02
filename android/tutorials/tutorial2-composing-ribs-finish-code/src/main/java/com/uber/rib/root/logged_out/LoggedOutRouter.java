package com.uber.rib.root.logged_out;

import com.uber.rib.core.ViewRouter;

/**
 * Adds and removes children of {@link LoggedOutBuilder.LoggedOutScope}.
 */
public class LoggedOutRouter extends
        ViewRouter<LoggedOutView, LoggedOutInteractor, LoggedOutBuilder.Component> {

    public LoggedOutRouter(
            LoggedOutView view,
            LoggedOutInteractor interactor,
            LoggedOutBuilder.Component component) {
        super(view, interactor, component);
    }
}
