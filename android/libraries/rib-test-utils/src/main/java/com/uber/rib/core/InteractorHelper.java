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

import androidx.annotation.Nullable;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;

/** The helper to test {@link Interactor}. */
public final class InteractorHelper {

  private InteractorHelper() {}

  /**
   * Attaches the {@link Interactor} inside a given root ViewGroup using a mock router.
   *
   * @param <P> the type of presenter.
   * @param <R> the type of router.
   * @param interactor the {@link Interactor}.
   * @param presenter the presenter for the {@link Interactor}.
   * @param router the mock router for the {@link Interactor}.
   * @param savedInstanceState the saved {@link Bundle}.
   */
  @SuppressWarnings("unchecked")
  public static <P, R extends Router> void attach(
      Interactor<P, R> interactor, P presenter, R router, @Nullable Bundle savedInstanceState) {
    interactor.presenter = presenter;
    interactor.setRouter(router);
    interactor.dispatchAttach(savedInstanceState);
  }

  /**
   * Detaches the {@link Interactor}.
   *
   * @param controller the {@link Interactor}.
   */
  public static void detach(Interactor controller) {
    controller.dispatchDetach();
  }

  /**
   * Verifies that the {@link Interactor} is attached.
   *
   * @param interactor the {@link Interactor}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(Interactor<?, ?> interactor) {
    verify(interactor).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)));
  }

  /**
   * Verifies that the {@link Interactor} is detached.
   *
   * @param interactor the {@link Interactor}.
   */
  public static void verifyDetached(Interactor interactor) {
    verify(interactor).dispatchDetach();
  }
}
