package com.badoo.common.rib.directory

import com.badoo.common.rib.ViewFactory
import com.uber.rib.core.RibAndroidView

abstract class LayoutCustomisation<T : RibAndroidView>(
    defaultLayoutResId: Int
) {
    val viewFactory: ViewFactory<T> = inflateOnDemand(defaultLayoutResId)
}
