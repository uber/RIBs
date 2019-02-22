package com.uber.rib.core.routing

import android.os.Bundle
import com.uber.rib.core.Router

interface RibConnector {
    fun addChild(childRouter: Router<*>, bundle: Bundle? = null)
    fun attachChildToView(childRouter: Router<*>)
    fun detachChildFromView(childRouter: Router<*>)
    fun removeChild(childRouter: Router<*>)

    companion object {
        fun from(
            addChild: (Router<*>, Bundle?) -> Unit,
            attachChildToView: (Router<*>) -> Unit,
            detachChildFromView: (Router<*>) -> Unit,
            removeChild: (Router<*>) -> Unit
        ) : RibConnector =
            object : RibConnector {
                override fun addChild(childRouter: Router<*>, bundle: Bundle?) {
                    addChild.invoke(childRouter, bundle)
                }

                override fun attachChildToView(childRouter: Router<*>) {
                    attachChildToView.invoke(childRouter)
                }

                override fun detachChildFromView(childRouter: Router<*>) {
                    detachChildFromView.invoke(childRouter)
                }

                override fun removeChild(childRouter: Router<*>) {
                    removeChild.invoke(childRouter)
                }
            }
    }
}
