package com.uber.rib.compose.root.main.logged_in.product_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.uber.rib.compose.EventStream
import com.uber.rib.compose.ExperimentsLocal
import com.uber.rib.compose.UButton

@Composable
fun ProductSelectionView(viewModel: State<ProductSelectionViewModel>, eventStream: EventStream) { // or use command/stream to intercept events for recording
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top,
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Blue)
  ) {
    Text("Product Sel (compose w/o CompView, no pres)", color = Color.White)
    Text("# of products available = ${viewModel.value.products.size}", color = Color.White)
    val xpClient = ExperimentsLocal.current
    val buttonLabel = if (remember { xpClient.isTreated("SOME_XP_NAME") }) {
      "TREATED"
    } else {
      "CONTROL"
    }
    UButton(analyticsId = "8bab05da-a7a0", onClick = { eventStream.notify("xp_button") }) {
      Text(buttonLabel, color = Color.White)
    }
    Clock(modifier = Modifier
      .size(250.dp)
      .padding(16.dp))
  }
}

@Preview
@Composable
fun ProductSelectionViewPreview() {
  val viewModel = remember {
    mutableStateOf(ProductSelectionViewModel(listOf("one", "two", "three")))
  }
  ProductSelectionView(viewModel, EventStream())
}
