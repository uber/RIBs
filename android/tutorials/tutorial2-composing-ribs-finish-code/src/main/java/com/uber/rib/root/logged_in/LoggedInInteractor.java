package com.uber.rib.root.logged_in;

import android.support.annotation.Nullable;

import com.uber.rib.core.Bundle;
import com.uber.rib.core.EmptyPresenter;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.core.Router;
import com.uber.rib.root.logged_in.off_game.OffGameInteractor;

import javax.inject.Inject;

/**
 * Coordinates Business Logic for {@link LoggedInScope}.
 *
 * TODO describe the logic of this scope.
 */
@RibInteractor
public class LoggedInInteractor extends Interactor<EmptyPresenter, LoggedInRouter> {

    @Override
    protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
        super.didBecomeActive(savedInstanceState);

        // when first logging in we should be in the OffGame state
        getRouter().attachOffGame();

    }

    class OffGameListener implements OffGameInteractor.Listener {

        @Override
        public void onStartGame() {
            getRouter().detachOffGame();
            getRouter().attachTicTacToe();
        }
    }

}
