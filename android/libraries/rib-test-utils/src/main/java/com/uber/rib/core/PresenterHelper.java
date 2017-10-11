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

/** The helper to test {@link Presenter}. */
public final class PresenterHelper {
  private PresenterHelper() {}

  /**
   * Loads the given {@link Presenter}.
   *
   * @param presenter the presenter.
   */
  public static void load(Presenter presenter) {
    presenter.dispatchLoad();
  }

  /**
   * Unloads the given {@link Presenter}.
   *
   * @param presenter the presenter.
   */
  public static void unload(Presenter presenter) {
    presenter.dispatchUnload();
  }
}
