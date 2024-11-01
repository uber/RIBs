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
package com.uber.rib.root.loggedin.tictactoe;

import javax.inject.Inject;

class Board {

  static final int ROWS = 3;
  static final int COLS = 3;

  MarkerType[][] cells;
  int currentRow;
  int currentCol;

  @Inject
  Board() {
    cells = new MarkerType[ROWS][COLS];
    for (int row = 0; row < ROWS; ++row) {
      for (int col = 0; col < COLS; ++col) {
        cells[row][col] = null;
      }
    }
  }

  /** Return true if it is a draw (i.e., no more EMPTY cell) */
  boolean isDraw() {
    for (int row = 0; row < ROWS; ++row) {
      for (int col = 0; col < COLS; ++col) {
        if (cells[row][col] == null) {
          return false;
        }
      }
    }
    return !hasWon(MarkerType.CROSS) && !hasWon(MarkerType.NOUGHT);
  }

  /** Return true if the player with "theSeed" has won after placing at (currentRow, currentCol) */
  boolean hasWon(MarkerType theSeed) {
    return ((cells[currentRow][0] == theSeed
            && cells[currentRow][1] == theSeed
            && cells[currentRow][2] == theSeed)
        || (cells[0][currentCol] == theSeed
            && cells[1][currentCol] == theSeed
            && cells[2][currentCol] == theSeed)
        || (currentRow == currentCol
            && cells[0][0] == theSeed
            && cells[1][1] == theSeed
            && cells[2][2] == theSeed)
        || (currentRow + currentCol == 2
            && cells[0][2] == theSeed
            && cells[1][1] == theSeed
            && cells[2][0] == theSeed));
  }

  enum MarkerType {
    CROSS,
    NOUGHT
  }
}
