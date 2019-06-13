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

package com.uber.rib.root.logged_in.tic_tac_toe;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.percentlayout.widget.PercentRelativeLayout;
import android.util.AttributeSet;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import com.uber.rib.core.Initializer;
import com.uber.rib.tutorial1.R;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.util.ArrayList;

/**
 * Top level view for {@link TicTacToeBuilder.TicTacToeScope}.
 */
public class TicTacToeView extends PercentRelativeLayout implements
    TicTacToeInteractor.TicTacToePresenter {

  private TextView[][] imageButtons;
  private TextView titleView;

  public TicTacToeView(Context context) {
    this(context, null);
  }

  public TicTacToeView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TicTacToeView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Initializer
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    imageButtons = new TextView[3][];
    imageButtons[0] =
        new TextView[]{
            (TextView) findViewById(R.id.button11),
            (TextView) findViewById(R.id.button12),
            (TextView) findViewById(R.id.button13)
        };
    imageButtons[1] =
        new TextView[]{
            (TextView) findViewById(R.id.button21),
            (TextView) findViewById(R.id.button22),
            (TextView) findViewById(R.id.button23)
        };
    imageButtons[2] =
        new TextView[]{
            (TextView) findViewById(R.id.button31),
            (TextView) findViewById(R.id.button32),
            (TextView) findViewById(R.id.button33)
        };
    titleView = (TextView) findViewById(R.id.title);
  }

  @Override
  public Observable<BoardCoordinate> squareClicks() {
    ArrayList<Observable<BoardCoordinate>> observables = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        final int finalI = i;
        final int finalJ = j;
        observables.add(
            RxView.clicks(imageButtons[i][j])
                .map(
                    new Function<Object, BoardCoordinate>() {
                      @Override
                      public BoardCoordinate apply(Object irrelevant) throws Exception {
                        return new BoardCoordinate(finalI, finalJ);
                      }
                    }));
      }
    }
    return Observable.merge(observables);
  }

  @Override
  public void addCross(BoardCoordinate xy) {
    TextView textView = imageButtons[xy.getX()][xy.getY()];
    textView.setText("x");
    textView.setClickable(false);
  }

  @Override
  public void addNought(BoardCoordinate xy) {
    TextView textView = imageButtons[xy.getX()][xy.getY()];
    textView.setText("O");
    textView.setClickable(false);
  }

  @Override
  public void setCurrentPlayerName(String currentPlayer) {
    titleView.setText("Current Player: " + currentPlayer);
  }

  @Override
  public void setPlayerWon(String playerName) {
    titleView.setText("Player won: " + playerName + "!!!");
  }

  @Override
  public void setPlayerTie() {
    titleView.setText("Tie game!");
  }
}
