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
package com.uber.rib.core

import com.badoo.ribs.core.Node
import com.uber.rib.util.RIBs

/** Debugging utilties when working with Nodes.  */
object NodeDebugUtils {

    private val ARM_RIGHT = "└── "
    private val INTERSECTION = "├── "
    private val LINE = "│   "
    private val SPACE = "    "

    /**
     * Prints out the tree of nodes from this point.
     *
     * @param node [Node]
     */
    fun printNodeSubtree(node: Node<*>) {
        printNodeSubtree(node, "", true)
    }

    private fun printNodeSubtree(
        node: Node<*>, prefix: String, isTail: Boolean
    ) {
        RIBs.getErrorHandler()
            .handleDebugMessage(prefix + (if (isTail) ARM_RIGHT else INTERSECTION) + node.tag)

        val children = node.getChildren()

        for (i in 0 until children.size - 1) {
            printNodeSubtree(children.get(i), prefix + if (isTail) SPACE else LINE, false)
        }

        if (children.size > 0) {
            printNodeSubtree(
                children.get(children.size - 1),
                prefix + if (isTail) SPACE else LINE,
                true
            )
        }
    }
}
