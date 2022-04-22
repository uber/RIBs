/*
 * Copyright (C) 2021. Uber Technologies
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
package com.uber.rib.compose.root.main.logged_in.tic_tac_toe

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uber.rib.compose.util.EventStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TicTacToeView(viewModel: State<TicTacToeViewModel>, eventStream: EventStream<TicTacToeEvent>) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top,
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Blue)
  ) {
    Text("Current Player: ${viewModel.value.currentPlayer}", color = Color.White)
    Box(
      modifier = Modifier
        .aspectRatio(1f)
        .fillMaxSize()
    ) {
      LazyVerticalGrid(cells = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
        val board = viewModel.value.board
        items(9) { i ->
          val row = i / 3
          val col = i % 3
          Text(
            text = when (board.cells[row][col]) {
              Board.MarkerType.CROSS -> "X"
              Board.MarkerType.NOUGHT -> "O"
              else -> " "
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1f)
              .padding(16.dp)
              .background(Color.LightGray)
              .clickable(
                enabled = board.cells[row][col] == null,
                onClick = {
                  eventStream.notify(TicTacToeEvent.BoardClick(BoardCoordinate(row, col)))
                }
              )
              .padding(32.dp)
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun ProductSelectionViewPreview() {
  val board = Board()
  board.cells[0][2] = Board.MarkerType.CROSS
  board.cells[1][0] = Board.MarkerType.NOUGHT
  board.cells[2][1] = Board.MarkerType.CROSS
  val viewModel = remember { mutableStateOf(TicTacToeViewModel("James", board)) }
  TicTacToeView(viewModel, EventStream())
}
