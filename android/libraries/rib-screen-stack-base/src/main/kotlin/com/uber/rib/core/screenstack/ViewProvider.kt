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
import com.uber.rib.core.screenstack.lifecycle.ScreenStackEvent
import io.reactivex.Observable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.rx2.asObservable

/** Interface to provide [View] instances to [ScreenStackBase]. */
abstract class ViewProvider {
  private val _lifecycleFlow = MutableSharedFlow<ScreenStackEvent>(1, 0, BufferOverflow.DROP_OLDEST)
  open val lifecycleFlow: SharedFlow<ScreenStackEvent>
    get() = _lifecycleFlow

  open fun buildViewInternal(parentView: ViewGroup): View {
    _lifecycleFlow.tryEmit(ScreenStackEvent.BUILT)
    return buildView(parentView)
  }

  /**
   * Builds a view to be displayed in a [ScreenStackBase] instance.
   *
   * @param parentView parent [ViewGroup] that the view will be displayed in.
   * @return the view to be displayed.
   */
  abstract fun buildView(parentView: ViewGroup): View

  /** @return an observable that emits events for this view provider's lifecycle. */
  fun lifecycle(): Observable<ScreenStackEvent> = lifecycleFlow.asObservable()

  /**
   * Callers can implement this in order to complete additional work when a call to
   * [ ][.onViewRemoved] is performed.
   */
  protected open fun doOnViewRemoved() {}

  /** Notifies the view provider that the view has been popped from the stack. */
  fun onViewRemoved() {
    _lifecycleFlow.tryEmit(ScreenStackEvent.REMOVED)
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

  /** Notifies the view provider that view is at the top of the stack and visible. */
  @CallSuper
  open fun onViewAppeared() {
    _lifecycleFlow.tryEmit(ScreenStackEvent.APPEARED)
  }

  /** Notifies the view provider that the view is no longer at the top of the stack. */
  @CallSuper
  open fun onViewHidden() {
    _lifecycleFlow.tryEmit(ScreenStackEvent.HIDDEN)
  }
}
