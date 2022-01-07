package com.uber.rib

import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import com.uber.rib.root.RootBuilder

class RootActivity : RibActivity() {

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *> {

        val rootBuilder = RootBuilder(object : RootBuilder.ParentComponent { })
        return rootBuilder.build(parentViewGroup)
    }
}
