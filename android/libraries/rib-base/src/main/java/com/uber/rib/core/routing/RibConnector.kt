package com.uber.rib.core.routing

import android.os.Bundle
import com.uber.rib.core.Router

interface RibConnector {
    /**
     * Implies attaching child router + view
     */
    fun attachChild(childRouter: Router<*>, bundle: Bundle? = null)

    /**
     * Attaches child view only. Expectation is that child router should already be attached.
     */
    fun attachChildView(childRouter: Router<*>)

    /**
     * Detaches child view only, child router remains alive
     */
    fun detachChildView(childRouter: Router<*>)

    /**
     * Detaches child router + view, killing it
     */
    fun detachChild(childRouter: Router<*>)

    companion object {
        fun from(
            attachChild: (Router<*>, Bundle?) -> Unit,
            attachChildView: (Router<*>) -> Unit,
            detachChildView: (Router<*>) -> Unit,
            detachChild: (Router<*>) -> Unit
        ) : RibConnector =
            object : RibConnector {
                override fun attachChild(childRouter: Router<*>, bundle: Bundle?) {
                    attachChild.invoke(childRouter, bundle)
                }

                override fun attachChildView(childRouter: Router<*>) {
                    attachChildView.invoke(childRouter)
                }

                override fun detachChildView(childRouter: Router<*>) {
                    detachChildView.invoke(childRouter)
                }

                override fun detachChild(childRouter: Router<*>) {
                    detachChild.invoke(childRouter)
                }
            }
    }
}
