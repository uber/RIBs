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

import org.mockito.InOrder;
import org.mockito.verification.VerificationMode;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/** The helper to test {@link Router}. */
public final class RouterHelper {

  private RouterHelper() {}

  /**
   * Dispatches attachment to a router.
   *
   * @param router to attach.
   * @param <R> type of router.
   */
  @SuppressWarnings("unchecked")
  public static <R extends Router> void attach(R router) {
    router.dispatchAttach(null);
  }

  /**
   * Detaches the {@link Router}.
   *
   * @param router the {@link Router}.
   */
  public static void detach(Router router) {
    router.dispatchDetach();
  }

  /**
   * Verifies that the {@link Router} is attached.
   *
   * @param router the {@link Router}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(Router router) {
    verify(router).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verifies that the {@link Router} is attached.
   *
   * @param order {@link InOrder} for ordered verification.
   * @param router the {@link Router}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(InOrder order, Router router) {
    order.verify(router).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verified that the {@link Router} is attached with a sepcific tag.
   *
   * @param router the {@link Router}.
   * @param tag the expected tag.
   */
  public static void verifyAttached(Router router, String tag) {
    verify(router).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), eq(tag));
  }

  /**
   * Verifies that the {@link Router} is attached with an additional {@link VerificationMode}.
   *
   * @param router the {@link Router}.
   * @param mode The mockito verification mode. ie. {@code times(1)}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(Router router, VerificationMode mode) {
    verify(router, mode).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verifies that the {@link Router} is not attached.
   *
   * @param router the {@link Router}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyNotAttached(Router router) {
    verify(router, never())
        .dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verifies that the {@link Router} is detached.
   *
   * @param router the {@link Router}.
   */
  public static void verifyDetached(Router router) {
    verify(router).dispatchDetach();
  }

  /**
   * Verifies that the {@link Router} is detached.
   *
   * @param order {@link InOrder} for ordered verification.
   * @param router the {@link Router}.
   */
  public static void verifyDetached(InOrder order, Router router) {
    order.verify(router).dispatchDetach();
  }

  /**
   * Verifies that the {@link Router} is detached with an additional {@link VerificationMode}.
   *
   * @param router the {@link Router}.
   * @param mode The mockito verification mode. ie. {@code times(1)}.
   */
  public static void verifyDetached(Router router, VerificationMode mode) {
    verify(router, mode).dispatchDetach();
  }

  /**
   * Verifies that the {@link Router} is detached.
   *
   * @param router the {@link Router}.
   */
  public static void verifyNotDetached(Router router) {
    verify(router, never()).dispatchDetach();
  }
}
