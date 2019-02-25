package com.uber.rib.core.directory

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.ViewFactory


fun <T> inflateOnDemand(@LayoutRes layoutResourceId: Int): ViewFactory<T> =
    object : ViewFactory<T> {
        override fun invoke(parentViewGroup: ViewGroup): T =
            inflate(parentViewGroup, layoutResourceId)
    }

fun <T> inflate(parentViewGroup: ViewGroup, @LayoutRes layoutResourceId: Int): T =
    LayoutInflater.from(parentViewGroup.context).inflate(layoutResourceId, parentViewGroup, false) as T
