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
package com.uber.rib.core;

import androidx.annotation.CallSuper;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.Relay;
import com.uber.autodispose.ScopeProvider;
import com.uber.rib.core.lifecycle.PresenterEvent;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.Observable;

import static com.uber.rib.core.lifecycle.PresenterEvent.LOADED;
import static com.uber.rib.core.lifecycle.PresenterEvent.UNLOADED;

/** Contains presentation logic. This class exists mainly for legacy reasons. In the past
 * we believed it was useful to have a class between interactors and views to facilitate model
 * transformations and believed these transformations would be complex enough to require its own
 * lifecycle. In practice this caused confusion: if both a presenter and interactor can perform
 * complex rx logic it becomes unclear where you should write your bussiness logic. */
public abstract class Presenter implements ScopeProvider {

  private final BehaviorRelay<PresenterEvent> behaviorRelay = BehaviorRelay.create();
  private final Relay<PresenterEvent> lifecycleRelay = behaviorRelay.toSerialized();

  private boolean isLoaded = false;

  protected void dispatchLoad() {
    isLoaded = true;
    lifecycleRelay.accept(LOADED);

    didLoad();
  }

  protected void dispatchUnload() {
    isLoaded = false;
    willUnload();

    lifecycleRelay.accept(UNLOADED);
  }

  /** @return {@code true} if the presenter is loaded, {@code false} if not. */
  protected boolean isLoaded() {
    return isLoaded;
  }

  /** Tells the presenter that it has finished loading. */
  @CallSuper
  protected void didLoad() {}

  /**
   * Tells the presenter that it will be destroyed. Presenter subclasses should perform any required
   * cleanup here.
   */
  @CallSuper
  protected void willUnload() {}

  /** @return an observable of this controller's lifecycle events. */
  public Observable<PresenterEvent> lifecycle() {
    return lifecycleRelay.hide();
  }

  @Override
  public CompletableSource requestScope() {
    return lifecycleRelay.skip(1).firstElement().ignoreElement();
  }
}
