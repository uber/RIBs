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

import android.view.View;

/**
 * This represents the portion of UI controlled by {@link Interactor}.
 *
 * @param <V> the view type.
 */
public abstract class ViewPresenter<V extends View> extends Presenter {

  private final V view;

  /**
   * Constructor.
   *
   * @param view The view to bind to.
   */
  public ViewPresenter(V view) {
    this.view = view;
  }

  /** @return the view fronted by the page. */
  public final V getView() {
    return view;
  }
}
