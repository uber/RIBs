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

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import com.uber.rib.core.Initializer;
import com.uber.rib.root.logged_in.GameKey;
import com.uber.rib.tutorial4.R;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Top level view for {@link OffGameBuilder.OffGameScope}.
 */
public class OffGameView extends LinearLayout implements OffGameInteractor.OffGamePresenter {

  private TextView playerOneName;
  private TextView playerTwoName;
  private TextView playerOneScore;
  private TextView playerTwoScore;

  public OffGameView(Context context) {
    this(context, null);
  }

  public OffGameView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public OffGameView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Initializer
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    playerOneName = (TextView) findViewById(R.id.player_one_name);
    playerTwoName = (TextView) findViewById(R.id.player_two_name);
    playerOneScore = (TextView) findViewById(R.id.player_one_win_count);
    playerTwoScore = (TextView) findViewById(R.id.player_two_win_count);
  }

  @Override
  public void setPlayerNames(String playerOne, String playerTwo) {
    playerOneName.setText(playerOne);
    playerTwoName.setText(playerTwo);
  }

  @Override
  public void setScores(Integer playerOneScore, Integer playerTwoScore) {
    this.playerOneScore.setText(
        String.format(Locale.getDefault(), "Win Count: %d", playerOneScore));
    this.playerTwoScore.setText(
        String.format(Locale.getDefault(), "Win Count: %d", playerTwoScore));
  }

  @Override
  public Observable<GameKey> startGameRequest(List<? extends GameKey> gameKeys) {
    List<Observable<GameKey>> observables = new ArrayList<>();
    for (final GameKey gameKey : gameKeys) {
      Button button = (Button) LayoutInflater.from(getContext()).inflate(R.layout.game_button, this, false);
      button.setText(gameKey.gameName());
      Observable<GameKey> observable = RxView
          .clicks(button)
          .map(new Function<Object, GameKey>() {
            @Override
            public GameKey apply(Object o) throws Exception {
              return gameKey;
            }
          });
      observables.add(observable);
      addView(button);
    }
    return Observable.merge(observables);
  }
}
