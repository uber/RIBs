package com.uber.rib.compose.root2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RootView() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = "eats") {
    composable("eats") {
      EatsFeed()
    }
    composable("rider") {  }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EatsFeed() {
  val feedItems = listOf(
    "Billboard", "Shortcuts", "Reorder", "Carousel", "Store", "Carousel", "Carousel", "Carousel", "Store", "Store",
    "Billboard", "Shortcuts", "Reorder", "Carousel", "Store", "Carousel", "Carousel", "Carousel", "Store", "Store",
    "Billboard", "Shortcuts", "Reorder", "Carousel", "Store", "Carousel", "Carousel", "Carousel", "Store", "Store")
  Surface {
    LazyColumn(
      contentPadding = PaddingValues(vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      itemsIndexed(feedItems) { index, feedItem ->
        Card(onClick = {}, elevation = 4.dp, modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()) {
          Text(text = "$feedItem $index", modifier = Modifier.padding(8.dp))
        }
      }
    }
  }
}

@Preview(widthDp = 128)
@Composable
fun EatsFeedPreview() {
  EatsFeed()
}
