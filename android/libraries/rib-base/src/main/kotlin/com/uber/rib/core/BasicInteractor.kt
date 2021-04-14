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
package com.uber.rib.core;

/**
 * {@link Interactor} that doesn't rely on field injection.
 *
 * @param <P> the type of {@link Presenter}.
 * @param <R> the type of {@link Router}.
 */
public abstract class BasicInteractor<P, R extends Router> extends Interactor<P, R> {

  @SuppressWarnings("HidingField")
  protected P presenter;

  protected BasicInteractor(P presenter) {
    super(presenter);
    this.presenter = presenter;
  }
}
