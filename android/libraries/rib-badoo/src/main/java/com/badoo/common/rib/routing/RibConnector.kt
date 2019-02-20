package com.badoo.common.rib.routing

import android.os.Bundle
import com.badoo.common.rib.BaseViewRouter

interface RibConnector {
    fun addChild(childRouter: BaseViewRouter<*, *>, bundle: Bundle? = null)
    fun attachChildToView(childRouter: BaseViewRouter<*, *>)
    fun detachChildFromView(childRouter: BaseViewRouter<*, *>)
    fun removeChild(childRouter: BaseViewRouter<*, *>)

    companion object {
        fun from(
            addChild: (BaseViewRouter<*, *>, Bundle?) -> Unit,
            attachChildToView: (BaseViewRouter<*, *>) -> Unit,
            detachChildFromView: (BaseViewRouter<*, *>) -> Unit,
            removeChild: (BaseViewRouter<*, *>) -> Unit
        ) : RibConnector =
            object : RibConnector {
                override fun addChild(childRouter: BaseViewRouter<*, *>, bundle: Bundle?) {
                    addChild.invoke(childRouter, bundle)
                }

                override fun attachChildToView(childRouter: BaseViewRouter<*, *>) {
                    attachChildToView.invoke(childRouter)
                }

                override fun detachChildFromView(childRouter: BaseViewRouter<*, *>) {
                    detachChildFromView.invoke(childRouter)
                }

                override fun removeChild(childRouter: BaseViewRouter<*, *>) {
                    removeChild.invoke(childRouter)
                }
            }
    }
}
