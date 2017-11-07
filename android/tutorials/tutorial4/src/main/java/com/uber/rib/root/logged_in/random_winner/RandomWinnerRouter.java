package com.uber.rib.root.logged_in.random_winner;

import com.uber.rib.core.ViewRouter;

/**
 * Adds and removes children of {@link RandomWinnerBuilder.RandomWinnerScope}.
 *
 * TODO describe the possible child configurations of this scope.
 */
public class RandomWinnerRouter extends
        ViewRouter<RandomWinnerView, RandomWinnerInteractor, RandomWinnerBuilder.Component> {

    public RandomWinnerRouter(
            RandomWinnerView view,
            RandomWinnerInteractor interactor,
            RandomWinnerBuilder.Component component) {
        super(view, interactor, component);
    }
}
