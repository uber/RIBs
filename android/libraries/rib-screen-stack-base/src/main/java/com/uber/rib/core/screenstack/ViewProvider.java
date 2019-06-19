/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.rib.core.screenstack;

import androidx.annotation.CallSuper;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.Relay;
import com.uber.rib.core.screenstack.lifecycle.ScreenStackEvent;

import io.reactivex.Observable;

/** Interface to provide {@link View} instances to {@link ScreenStackBase}. */
public abstract class ViewProvider {

  private final Relay<ScreenStackEvent> lifecycleRelay =
      BehaviorRelay.<ScreenStackEvent>create().toSerialized();

  View buildViewInternal(ViewGroup parentView) {
    lifecycleRelay.accept(ScreenStackEvent.BUILT);
    return buildView(parentView);
  }

  /**
   * Builds a view to be displayed in a {@link ScreenStackBase} instance.
   *
   * @param parentView parent {@link ViewGroup} that the view will be displayed in.
   * @return the view to be displayed.
   */
  public abstract View buildView(ViewGroup parentView);

  /** @return an observable that emits events for this view provider's lifecycle. */
  public Observable<ScreenStackEvent> lifecycle() {
    return lifecycleRelay.hide();
  }

  /**
   * Callers can implement this in order to complete additional work when a call to {@link
   * #onViewRemoved()} is performed.
   */
  protected void doOnViewRemoved() {}

  /** Notifies the view provider that the view has been popped from the stack. */
  public final void onViewRemoved() {
    lifecycleRelay.accept(ScreenStackEvent.REMOVED);
    doOnViewRemoved();
  }

  /**
   * Asks the view provider to handle a back press.
   *
   * @return TRUE if the provider handled the back press.
   */
  public boolean onBackPress() {
    return false;
  }

  /** Notifies the view provider that view is at the top of the stack and visible. */
  @CallSuper
  public void onViewAppeared() {
    lifecycleRelay.accept(ScreenStackEvent.APPEARED);
  }

  /** Notifies the view provider that the view is no longer at the top of the stack. */
  @CallSuper
  public void onViewHidden() {
    lifecycleRelay.accept(ScreenStackEvent.HIDDEN);
  }
}
