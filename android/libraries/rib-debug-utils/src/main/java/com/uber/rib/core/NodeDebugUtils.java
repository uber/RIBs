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

import com.badoo.ribs.core.Node;
import com.uber.rib.util.RIBs;

import java.util.List;

/** Debugging utilties when working with Routers. */
public final class NodeDebugUtils {
  private static final String ARM_RIGHT = "└── ";
  private static final String INTERSECTION = "├── ";
  private static final String LINE = "│   ";
  private static final String SPACE = "    ";

  private NodeDebugUtils() {}

  /**
   * Prints out the tree of routers from this point.
   *
   * @param node {@link Node}
   */
  public static void printNodeSubtree(final Node<?> node) {
    printNodeSubtree(node, "", true);
  }

  private static void printNodeSubtree(
      final Node<?> node, final String prefix, final boolean isTail) {
    RIBs.getErrorHandler()
        .handleDebugMessage(prefix + (isTail ? ARM_RIGHT : INTERSECTION) + node.getTag());

    List<Node<?>> children = node.getChildren();

    for (int i = 0; i < children.size() - 1; i++) {
      printNodeSubtree(children.get(i), prefix + (isTail ? SPACE : LINE), false);
    }

    if (children.size() > 0) {
      printNodeSubtree(children.get(children.size() - 1), prefix + (isTail ? SPACE : LINE), true);
    }
  }
}
