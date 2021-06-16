package com.uber.rib.compose.root.main.logged_in.product_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava2.subscribeAsState
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.rib.compose.EventStream
import com.uber.rib.core.ComposePresenter

@motif.Scope
interface ProductSelectionScope {
  fun router(): ProductSelectionRouter

  @motif.Objects
  abstract class Objects {
    abstract fun router(): ProductSelectionRouter

    abstract fun interactor(): ProductSelectionInteractor

    fun presenter(viewModelStream: BehaviorRelay<ProductSelectionViewModel>, eventStream: EventStream): ComposePresenter {
      return object : ComposePresenter() {
        override val composable = @Composable {
          ProductSelectionView(viewModelStream.subscribeAsState(initial = viewModelStream.value!!), eventStream)
        }
      }
    }

    fun viewModelStream() = BehaviorRelay.createDefault(ProductSelectionViewModel())

    abstract fun eventStream(): EventStream
  }
}
