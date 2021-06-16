package com.uber.rib.compose.root.main.logged_out

import com.uber.autodispose.autoDispose
import com.uber.rib.compose.root.main.AuthStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import io.reactivex.Observable

class LoggedOutInteractor(
  presenter: LoggedOutPresenter,
  private val authStream: AuthStream
) : BasicInteractor<LoggedOutInteractor.LoggedOutPresenter, LoggedOutRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
    presenter.loginClicks()
      .autoDispose(this)
      .subscribe { authStream.accept(true) }
  }

  interface LoggedOutPresenter {
    fun loginClicks(): Observable<Unit>
  }
}
