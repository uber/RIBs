/*
 * Copyright (C) 2017. Uber Technologies
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
package com.uber.rib.core.screenstack

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.rib.core.screenstack.lifecycle.ScreenStackEvent
import io.reactivex.Observable

/** Interface to provide [View] instances to [ScreenStackBase].  */
abstract class ViewProvider {
  private val lifecycleRelay = BehaviorRelay.create<ScreenStackEvent>().toSerialized()

  open fun buildViewInternal(parentView: ViewGroup): View {
    lifecycleRelay.accept(ScreenStackEvent.BUILT)
    return buildView(parentView)
  }

  /**
   * Builds a view to be displayed in a [ScreenStackBase] instance.
   *
   * @param parentView parent [ViewGroup] that the view will be displayed in.
   * @return the view to be displayed.
   */
  abstract fun buildView(parentView: ViewGroup): View

  /** @return an observable that emits events for this view provider's lifecycle.
   */
  open fun lifecycle(): Observable<ScreenStackEvent> {
    return lifecycleRelay.hide()
  }

  /**
   * Callers can implement this in order to complete additional work when a call to [ ][.onViewRemoved] is performed.
   */
  protected open fun doOnViewRemoved() {}

  /** Notifies the view provider that the view has been popped from the stack.  */
  fun onViewRemoved() {
    lifecycleRelay.accept(ScreenStackEvent.REMOVED)
    doOnViewRemoved()
  }

  /**
   * Asks the view provider to handle a back press.
   *
   * @return TRUE if the provider handled the back press.
   */
  open fun onBackPress(): Boolean {
    return false
  }

  /** Notifies the view provider that view is at the top of the stack and visible.  */
  @CallSuper
  open fun onViewAppeared() {
    lifecycleRelay.accept(ScreenStackEvent.APPEARED)
  }

  /** Notifies the view provider that the view is no longer at the top of the stack.  */
  @CallSuper
  open fun onViewHidden() {
    lifecycleRelay.accept(ScreenStackEvent.HIDDEN)
  }
}
