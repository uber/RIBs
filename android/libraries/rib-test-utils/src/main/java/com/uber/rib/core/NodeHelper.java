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

import android.os.Bundle;

import org.mockito.InOrder;
import org.mockito.verification.VerificationMode;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/** The helper to test {@link Node}. */
public final class NodeHelper {

  private NodeHelper() {}

  /**
   * Dispatches attachment to a node.
   *
   * @param router to attach.
   * @param <R> type of node.
   */
  @SuppressWarnings("unchecked")
  public static <R extends Node> void attach(R router) {
    router.dispatchAttach(null);
  }

  /**
   * Detaches the {@link Node}.
   *
   * @param node the {@link Node}.
   */
  public static void detach(Node node) {
    node.dispatchDetach();
  }

  /**
   * Verifies that the {@link Node} is attached.
   *
   * @param node the {@link Node}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(Node node) {
    verify(node).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verifies that the {@link Node} is attached.
   *
   * @param order {@link InOrder} for ordered verification.
   * @param node the {@link Node}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(InOrder order, Node node) {
    order.verify(node).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verified that the {@link Node} is attached with a sepcific tag.
   *
   * @param node the {@link Node}.
   * @param tag the expected tag.
   */
  public static void verifyAttached(Node node, String tag) {
    verify(node).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), eq(tag));
  }

  /**
   * Verifies that the {@link Node} is attached with an additional {@link VerificationMode}.
   *
   * @param node the {@link Node}.
   * @param mode The mockito verification mode. ie. {@code times(1)}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyAttached(Node node, VerificationMode mode) {
    verify(node, mode).dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verifies that the {@link Node} is not attached.
   *
   * @param node the {@link Node}.
   */
  @SuppressWarnings("unchecked")
  public static void verifyNotAttached(Node node) {
    verify(node, never())
        .dispatchAttach(or(isNull(Bundle.class), isA(Bundle.class)), anyString());
  }

  /**
   * Verifies that the {@link Node} is detached.
   *
   * @param node the {@link Node}.
   */
  public static void verifyDetached(Node node) {
    verify(node).dispatchDetach();
  }

  /**
   * Verifies that the {@link Node} is detached.
   *
   * @param order {@link InOrder} for ordered verification.
   * @param node the {@link Node}.
   */
  public static void verifyDetached(InOrder order, Node node) {
    order.verify(node).dispatchDetach();
  }

  /**
   * Verifies that the {@link Node} is detached with an additional {@link VerificationMode}.
   *
   * @param node the {@link Node}.
   * @param mode The mockito verification mode. ie. {@code times(1)}.
   */
  public static void verifyDetached(Node node, VerificationMode mode) {
    verify(node, mode).dispatchDetach();
  }

  /**
   * Verifies that the {@link Node} is detached.
   *
   * @param node the {@link Node}.
   */
  public static void verifyNotDetached(Node node) {
    verify(node, never()).dispatchDetach();
  }
}
