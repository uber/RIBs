package com.uber.rib.compose.root.main

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.uber.rib.compose.R
import com.uber.rib.compose.UButton

@Composable
fun MainView(viewModel: State<MainViewModel>, onRestartClick: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top,
    modifier = Modifier
      .fillMaxSize()
      .padding(all = 4.dp)
      .padding(top = 14.dp)
      .background(Color(0xFFFFA500))
  ) {
    Text("Main RIB (compose w/ CompView, w/ pres) | count = ${viewModel.value.count}")
    UButton(
      onClick = onRestartClick,
      analyticsId = "e775e7ae-c434",
      modifier = Modifier.padding(8.dp)
    ) {
      Text(text = "Restart")
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1.0f)
        .padding(4.dp)
        .background(Color.Yellow)) {

      AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
          FrameLayout(context).apply {
            id = R.id.login_logout_container
            setBackgroundColor(android.graphics.Color.DKGRAY)
          }
        }
      )
    }
  }
}

@Preview
@Composable
fun MainViewPreview() {
  val viewModel = remember { mutableStateOf(MainViewModel(count = 42)) }
  MainView(viewModel, {})
}