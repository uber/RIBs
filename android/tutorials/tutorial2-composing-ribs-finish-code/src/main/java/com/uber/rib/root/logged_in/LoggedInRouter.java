package com.uber.rib.root.logged_in;

import com.uber.rib.core.Router;
import com.uber.rib.root.RootView;
import com.uber.rib.root.logged_in.off_game.OffGameBuilder;
import com.uber.rib.root.logged_in.off_game.OffGameRouter;
import com.uber.rib.root.logged_in.tic_tac_toe.TicTacToeBuilder;
import com.uber.rib.root.logged_in.tic_tac_toe.TicTacToeRouter;

/**
 * Adds and removes children of {@link LoggedInBuilder.LoggedInScope}.
 */
public class LoggedInRouter
        extends Router<LoggedInInteractor, LoggedInBuilder.Component> {

    private final OffGameBuilder offGameBuilder;
    private final TicTacToeBuilder ticTacToeBuilder;
    private final RootView rootView;
    private OffGameRouter offGameRouter;

    LoggedInRouter(
            LoggedInInteractor interactor,
            LoggedInBuilder.Component component,
            OffGameBuilder offGameBuilder,
            TicTacToeBuilder ticTacToeBuilder,
            RootView rootView) {
        super(interactor, component);
        this.offGameBuilder = offGameBuilder;
        this.ticTacToeBuilder = ticTacToeBuilder;
        this.rootView = rootView;
    }

    void detachOffGame() {
        detachChild(offGameRouter);
        rootView.removeView(offGameRouter.getView());
    }

    void attachTicTacToe() {
        TicTacToeRouter ticTacToeRouter = ticTacToeBuilder.build(rootView);
        rootView.addView(ticTacToeRouter.getView());
        attachChild(ticTacToeRouter);
    }

    void attachOffGame() {
        offGameRouter = offGameBuilder.build(rootView);
        rootView.addView(offGameRouter.getView());
        attachChild(offGameRouter);
    }
}
