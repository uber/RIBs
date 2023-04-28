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
@file:Suppress("invisible_reference", "invisible_member")

package com.uber.rib.core

/** Debugging utilities when working with Routers. */
public object RouterDebugUtils {
  private const val ARM_RIGHT = "└── "
  private const val INTERSECTION = "├── "
  private const val LINE = "│   "
  private const val SPACE = "    "

  /**
   * Prints out the tree of routers from this point.
   *
   * @param router [Router] root router of a RIB tree
   * @param prefix [String] text to print before the tree
   * @param isTail [Boolean] true if is tail node; otherwise, false
   */
  @JvmStatic
  @JvmOverloads
  public fun printRouterSubtree(router: Router<*>, prefix: String = "", isTail: Boolean = true) {
    Rib.getConfiguration()
      .handleDebugMessage(prefix + (if (isTail) ARM_RIGHT else INTERSECTION) + router.tag)
    val children = router.getChildren()
    for (i in 0 until children.size - 1) {
      printRouterSubtree(children[i], prefix + if (isTail) SPACE else LINE, false)
    }
    if (children.size > 0) {
      printRouterSubtree(children[children.size - 1], prefix + if (isTail) SPACE else LINE, true)
    }
  }
}
