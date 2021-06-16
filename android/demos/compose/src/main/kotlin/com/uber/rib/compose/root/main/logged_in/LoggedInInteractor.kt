package com.uber.rib.compose.root.main.logged_in

import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import com.uber.rib.compose.root.main.AuthStream
import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.Bundle
import com.uber.rib.core.EmptyPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoggedInInteractor(
  presenter: EmptyPresenter,
  private val authStream: AuthStream
) : BasicInteractor<EmptyPresenter, LoggedInRouter>(presenter) {

  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
   // subscribe to authstream and route
    asCoroutineScope().launch {
      delay(3000)
      withContext(Dispatchers.Main) { router.attachProductSelection() }
    }
  }
}
