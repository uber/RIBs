package com.uber.rib.compose.root.main

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

class AuthStream {
  private val authRelay = BehaviorRelay.createDefault(false)

  fun observe(): Observable<Boolean> = authRelay.hide()

  fun accept(value: Boolean) {
    authRelay.accept(value)
  }
}
