package com.badoo.ribs.core.routing

import android.os.Bundle
import com.badoo.ribs.core.Node

interface RibConnector {
    /**
     * Implies attaching child node + view
     */
    fun attachChild(childNode: Node<*>, bundle: Bundle? = null)

    /**
     * Attaches child view only. Expectation is that child node should already be attached.
     */
    fun attachChildView(childNode: Node<*>)

    /**
     * Detaches child view only, child node remains alive
     */
    fun detachChildView(childNode: Node<*>)

    /**
     * Detaches child node + view, killing it
     */
    fun detachChild(childNode: Node<*>)

    companion object {
        fun from(
            attachChild: (Node<*>, Bundle?) -> Unit,
            attachChildView: (Node<*>) -> Unit,
            detachChildView: (Node<*>) -> Unit,
            detachChild: (Node<*>) -> Unit
        ) : RibConnector =
            object : RibConnector {
                override fun attachChild(childNode: Node<*>, bundle: Bundle?) {
                    attachChild.invoke(childNode, bundle)
                }

                override fun attachChildView(childNode: Node<*>) {
                    attachChildView.invoke(childNode)
                }

                override fun detachChildView(childNode: Node<*>) {
                    detachChildView.invoke(childNode)
                }

                override fun detachChild(childNode: Node<*>) {
                    detachChild.invoke(childNode)
                }
            }
    }
}
