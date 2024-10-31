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
package com.uber.rib.root.loggedin;

import com.google.common.collect.ImmutableMap;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.uber.rib.root.UserName;
import io.reactivex.Observable;
import java.util.Map;

class MutableScoreStream implements ScoreStream {

  private final BehaviorRelay<ImmutableMap<UserName, Integer>> scoresRelay = BehaviorRelay.create();

  MutableScoreStream(UserName playerOne, UserName playerTwo) {
    scoresRelay.accept(ImmutableMap.of(playerOne, 0, playerTwo, 0));
  }

  void addVictory(UserName userName) {
    ImmutableMap<UserName, Integer> currentScores = scoresRelay.getValue();

    ImmutableMap.Builder<UserName, Integer> newScoreMapBuilder = new ImmutableMap.Builder<>();
    for (Map.Entry<UserName, Integer> entry : currentScores.entrySet()) {
      if (entry.getKey().equals(userName)) {
        newScoreMapBuilder.put(entry.getKey(), entry.getValue() + 1);
      } else {
        newScoreMapBuilder.put(entry.getKey(), entry.getValue());
      }
    }

    scoresRelay.accept(newScoreMapBuilder.build());
  }

  @Override
  public Observable<ImmutableMap<UserName, Integer>> scores() {
    return scoresRelay.hide();
  }
}
