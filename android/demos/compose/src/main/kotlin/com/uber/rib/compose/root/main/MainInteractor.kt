package com.uber.rib.compose.root.main

import com.uber.autodispose.autoDispose
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.rx2.asFlow
import java.util.concurrent.TimeUnit

class MainInteractor(
  presenter: MainPresenter,
  private val authStream: AuthStream
) : BasicInteractor<MainPresenter, MainRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
    Observable.interval(1, TimeUnit.SECONDS)
      .asFlow()
      .onEach { presenter.incrementCount() }
      .launchIn(asCoroutineScope())

    authStream.observe()
      .subscribeOn(Schedulers.io())
      .flatMap { isLoggedIn ->
        while (!router.view.hasComposition) {
          Thread.sleep(100)
        }
        Observable.just(isLoggedIn)
      }
      .observeOn(mainThread())
      .autoDispose(this)
      .subscribe { isLoggedIn ->
        if (isLoggedIn) {
          router.detachLoggedOut()
          router.attachLoggedIn()
        } else {
          router.detachLoggedIn()
          router.attachLoggedOut()
        }
      }
  }
}
