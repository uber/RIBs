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

/**
 * Designates a component that can provide a specific interactor. This is identical to [ ]. This
 * exists soley for backwards compatibility with old versions of the Presidio Intellij Plugin.
 *
 * @param <T> type of interactor that is injected.
 * @param <P> type of presenter.
 */
public interface InteractorComponent<P : Presenter, T : Interactor<P, *>> :
  InteractorBaseComponent<T> {
  /**
   * Inject the interactor.
   *
   * @param interactor to inject.
   */
  override fun inject(interactor: T)

  /**
   * The presenter.
   *
   * @return the presenter.
   */
  public fun presenter(): P
}
