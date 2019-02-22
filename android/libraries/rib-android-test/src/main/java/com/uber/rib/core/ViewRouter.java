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

/**
 * Router subclass that has a view.
 *
 * @param <V> type of view owned by the router.
 * @param <I> type of interactor owned by the router.
 */
public abstract class ViewRouter<V extends RibView, I extends Interactor<V, ?>>
    extends Router<V> {

  private final V view;

  public ViewRouter(V view, I interactor) {
    super(null, interactor, RibRefWatcher.getInstance());
    this.view = view;
  }

  /** @return the router's view. */
  public V getView() {
    return view;
  }
}
