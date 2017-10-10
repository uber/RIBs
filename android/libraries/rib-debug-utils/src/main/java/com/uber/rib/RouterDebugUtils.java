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

import java.util.List;

/** Debugging utilties when working with Routers. */
public final class RouterDebugUtils {
  private static final String ARM_RIGHT = "└── ";
  private static final String INTERSECTION = "├── ";
  private static final String LINE = "│   ";
  private static final String SPACE = "    ";

  private RouterDebugUtils() {}

  /**
   * Prints out the tree of routers from this point.
   *
   * @param router {@link Router}
   */
  public static void printRouterSubtree(final Router<?, ?> router) {
    printRouterSubtree(router, "", true);
  }

  private static void printRouterSubtree(
      final Router<?, ?> router, final String prefix, final boolean isTail) {
    Rib.getConfiguration()
        .handleDebugMessage(prefix + (isTail ? ARM_RIGHT : INTERSECTION) + router.getTag());

    List<Router> children = router.getChildren();

    for (int i = 0; i < children.size() - 1; i++) {
      printRouterSubtree(children.get(i), prefix + (isTail ? SPACE : LINE), false);
    }

    if (children.size() > 0) {
      printRouterSubtree(children.get(children.size() - 1), prefix + (isTail ? SPACE : LINE), true);
    }
  }
}
