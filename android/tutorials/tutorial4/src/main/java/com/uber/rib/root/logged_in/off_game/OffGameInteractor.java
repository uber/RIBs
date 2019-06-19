/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.rib.root.logged_in.off_game;

import androidx.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import com.uber.autodispose.AutoDispose;
import com.uber.rib.core.Bundle;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.root.UserName;
import com.uber.rib.root.logged_in.GameKey;
import com.uber.rib.root.logged_in.ScoreStream;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import javax.inject.Inject;
import javax.inject.Named;

@RibInteractor
public class OffGameInteractor
    extends Interactor<OffGameInteractor.OffGamePresenter, OffGameRouter> {

  @Inject @Named("player_one") UserName playerOne;
  @Inject @Named("player_two") UserName playerTwo;
  @Inject Listener listener;
  @Inject OffGamePresenter presenter;
  @Inject ScoreStream scoreStream;
  @Inject List<? extends GameKey> gameNames;


  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);

    presenter.setPlayerNames(playerOne.getUserName(), playerTwo.getUserName());
    presenter
        .startGameRequest(gameNames)
        .subscribe(new Consumer<GameKey>() {
          @Override
          public void accept(GameKey gameKey) throws Exception {
            listener.onStartGame(gameKey);
          }
        });

    scoreStream.scores()
        .as(AutoDispose.<ImmutableMap<UserName, Integer>>autoDisposable(this))
        .subscribe(new Consumer<ImmutableMap<UserName,Integer>>() {
          @Override
          public void accept(ImmutableMap<UserName, Integer> scores)
              throws Exception {
            Integer playerOneScore = scores.get(playerOne);
            Integer playerTwoScore = scores.get(playerTwo);
            presenter.setScores(playerOneScore, playerTwoScore);
          }
        });
  }

  public interface Listener {

    void onStartGame(GameKey gameKey);
  }

  /**
   * Presenter interface implemented by this RIB's view.
   */
  interface OffGamePresenter {

    void setPlayerNames(String playerOne, String playerTwo);

    void setScores(Integer playerOneScore, Integer playerTwoScore);

    Observable<GameKey> startGameRequest(List<? extends GameKey> gameKeys);
  }
}
