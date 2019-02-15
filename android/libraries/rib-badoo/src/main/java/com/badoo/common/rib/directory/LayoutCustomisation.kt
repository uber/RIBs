package com.badoo.common.rib.directory

import com.badoo.common.rib.ViewFactory
import com.uber.rib.core.RibView

abstract class LayoutCustomisation<T : RibView>(
    defaultLayoutResId: Int
) {
    val viewFactory: ViewFactory<T> = inflateOnDemand(defaultLayoutResId)
}
