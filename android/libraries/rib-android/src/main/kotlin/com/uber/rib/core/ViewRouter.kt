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
package com.uber.rib.core

import android.view.View

/**
 * Router subclass that has a view.
 *
 * @param <V> type of view owned by the router.
 * @param <I> type of interactor owned by the router.
 */
abstract class ViewRouter<V : View, I : Interactor<*, *>> : Router<I> {
  /** @return the router's view. */
  open val view: V

  constructor(
    view: V,
    interactor: I,
    component: InteractorBaseComponent<*>,
  ) : super(interactor, component) {
    this.view = view
    if (XRay.isEnabled()) {
      XRay.apply(this, view)
    }
  }

  protected constructor(
    view: V,
    interactor: I,
  ) : super(null, interactor, RibRefWatcher.getInstance(), getMainThread()) {
    this.view = view
    if (XRay.isEnabled()) {
      XRay.apply(this, view)
    }
  }

  internal fun saveInstanceStateInternal(outState: Bundle) {
    saveInstanceState(outState)
  }
}
