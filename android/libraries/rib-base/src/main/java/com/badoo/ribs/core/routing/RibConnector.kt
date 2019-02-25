package com.badoo.ribs.core.routing

import android.os.Bundle
import com.badoo.ribs.core.Node

interface RibConnector {
    /**
     * Implies attaching child node + view
     */
    fun attachChild(childRouter: Node<*>, bundle: Bundle? = null)

    /**
     * Attaches child view only. Expectation is that child node should already be attached.
     */
    fun attachChildView(childRouter: Node<*>)

    /**
     * Detaches child view only, child node remains alive
     */
    fun detachChildView(childRouter: Node<*>)

    /**
     * Detaches child node + view, killing it
     */
    fun detachChild(childRouter: Node<*>)

    companion object {
        fun from(
            attachChild: (Node<*>, Bundle?) -> Unit,
            attachChildView: (Node<*>) -> Unit,
            detachChildView: (Node<*>) -> Unit,
            detachChild: (Node<*>) -> Unit
        ) : RibConnector =
            object : RibConnector {
                override fun attachChild(childRouter: Node<*>, bundle: Bundle?) {
                    attachChild.invoke(childRouter, bundle)
                }

                override fun attachChildView(childRouter: Node<*>) {
                    attachChildView.invoke(childRouter)
                }

                override fun detachChildView(childRouter: Node<*>) {
                    detachChildView.invoke(childRouter)
                }

                override fun detachChild(childRouter: Node<*>) {
                    detachChild.invoke(childRouter)
                }
            }
    }
}
