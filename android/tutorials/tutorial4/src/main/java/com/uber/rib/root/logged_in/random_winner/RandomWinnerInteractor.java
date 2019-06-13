package com.uber.rib.root.logged_in.random_winner;

import androidx.annotation.Nullable;

import com.uber.rib.core.Bundle;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.root.UserName;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

@RibInteractor
public class RandomWinnerInteractor
        extends Interactor<RandomWinnerInteractor.RandomWinnerPresenter, RandomWinnerRouter> {

    @Inject Listener listener;
    @Inject @Named("player_one") UserName playerOne;
    @Inject @Named("player_two") UserName playerTwo;

    @Override
    protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
        super.didBecomeActive(savedInstanceState);
        if (new Random(System.currentTimeMillis()).nextBoolean()) {
            listener.gameWon(playerOne);
        } else {
            listener.gameWon(playerTwo);
        }
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface RandomWinnerPresenter { }

    public interface Listener {

        /**
         * Called when the game is over.
         *
         * @param winner player that won, or null if it's a tie.
         */
        void gameWon(UserName winner);
    }
}
