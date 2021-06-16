package com.uber.rib.compose.root.main.logged_in.product_selection

import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.autodispose.autoDispose
import com.uber.rib.compose.EventStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.ComposePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.computation
import java.util.concurrent.TimeUnit

class ProductSelectionInteractor(
  presenter: ComposePresenter,
  private val eventStream: EventStream,
  private val viewModelStream: BehaviorRelay<ProductSelectionViewModel>
) : BasicInteractor<ComposePresenter, ProductSelectionRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
//    eventStream // TODO: di this in from the scope via the ctor
//      .filter { it.name == "next"}
//      .subscribe {
//          router.goToHome()
//      }

    Observable.interval(1, TimeUnit.SECONDS)
      .startWith(0L)
      .subscribeOn(computation())
      .map { it.toInt() }
      .observeOn(mainThread())
      .autoDispose(this)
      .subscribe {
        val products = viewModelStream.value!!.products.toMutableList()
        if (it % 5 == 0) {
          products.clear()
        }
        products += "Product $it"
        viewModelStream.accept(viewModelStream.value!!.copy(products = products))
      }
  }
}
