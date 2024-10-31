/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.root.loggedin.randomWinner;

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

  @Inject
  @Named("player_one")
  UserName playerOne;

  @Inject
  @Named("player_two")
  UserName playerTwo;

  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);
    if (new Random(System.currentTimeMillis()).nextBoolean()) {
      listener.gameWon(playerOne);
    } else {
      listener.gameWon(playerTwo);
    }
  }

  /** Presenter interface implemented by this RIB's view. */
  interface RandomWinnerPresenter {}

  public interface Listener {

    /**
     * Called when the game is over.
     *
     * @param winner player that won, or null if it's a tie.
     */
    void gameWon(UserName winner);
  }
}
