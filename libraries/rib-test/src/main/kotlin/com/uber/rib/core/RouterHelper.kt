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

import org.mockito.AdditionalMatchers.or
import org.mockito.InOrder
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.isA
import org.mockito.kotlin.isNull
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.verification.VerificationMode

/** The helper to test [Router]. */
public object RouterHelper {
  /**
   * Dispatches attachment to a router.
   *
   * @param router to attach.
   * @param <R> type of router.
   */
  @JvmStatic
  public fun <R : Router<*>> attach(router: R) {
    router.dispatchAttach(null)
  }

  /**
   * Detaches the [Router].
   *
   * @param router the [Router].
   */
  @JvmStatic
  public fun detach(router: Router<*>) {
    router.dispatchDetach()
  }

  /**
   * Verifies that the [Router] is attached.
   *
   * @param router the [Router].
   */
  @JvmStatic
  public fun verifyAttached(router: Router<*>) {
    verify(router).dispatchAttach(or(isNull(), isA<Bundle>()), any())
  }

  /**
   * Verifies that the [Router] was attached a certain number of times
   *
   * @param router the [Router].
   * @param times the number of times attached
   */
  @JvmStatic
  public fun verifyAttached(router: Router<*>, times: Int) {
    verify(router, times(times)).dispatchAttach(or(isNull(), isA<Bundle>()), any())
  }

  /**
   * Verifies that the [Router] is attached.
   *
   * @param order [InOrder] for ordered verification.
   * @param router the [Router].
   */
  @JvmStatic
  public fun verifyAttached(order: InOrder, router: Router<*>) {
    order.verify(router).dispatchAttach(or(isNull(), isA<Bundle>()), any())
  }

  /**
   * Verified that the [Router] is attached with a specific tag.
   *
   * @param router the [Router].
   * @param tag the expected tag.
   */
  @JvmStatic
  public fun verifyAttached(router: Router<*>, tag: String) {
    verify(router).dispatchAttach(or(isNull(), isA<Bundle>()), eq(tag))
  }

  /**
   * Verifies that the [Router] is attached with an additional [VerificationMode].
   *
   * @param router the [Router].
   * @param mode The mockito verification mode. ie. `times(1)`.
   */
  @JvmStatic
  public fun verifyAttached(router: Router<*>, mode: VerificationMode) {
    verify(router, mode).dispatchAttach(or(isNull(), isA<Bundle>()), any())
  }

  /**
   * Verifies that the [Router] is not attached.
   *
   * @param router the [Router].
   */
  @JvmStatic
  public fun verifyNotAttached(router: Router<*>) {
    verify(router, never()).dispatchAttach(or(isNull(), isA<Bundle>()), any())
  }

  /**
   * Verifies that the [Router] is detached.
   *
   * @param router the [Router].
   */
  @JvmStatic
  public fun verifyDetached(router: Router<*>) {
    verify(router).dispatchDetach()
  }

  /**
   * Verifies that the [Router] is detached.
   *
   * @param order [InOrder] for ordered verification.
   * @param router the [Router].
   */
  @JvmStatic
  public fun verifyDetached(order: InOrder, router: Router<*>) {
    order.verify(router).dispatchDetach()
  }

  /**
   * Verifies that the [Router] is detached with an additional [VerificationMode].
   *
   * @param router the [Router].
   * @param mode The mockito verification mode. ie. `times(1)`.
   */
  @JvmStatic
  public fun verifyDetached(router: Router<*>, mode: VerificationMode) {
    verify(router, mode).dispatchDetach()
  }

  /**
   * Verifies that the [Router] is not detached.
   *
   * @param router the [Router].
   */
  @JvmStatic
  public fun verifyNotDetached(router: Router<*>) {
    verify(router, never()).dispatchDetach()
  }
}
