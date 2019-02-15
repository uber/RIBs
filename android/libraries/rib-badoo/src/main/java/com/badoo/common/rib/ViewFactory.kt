package com.badoo.common.rib

import android.view.ViewGroup

// this is only temporary, as I did not want to spend time with refactoring typealias to interface everywhere
interface IViewFactory<T>: ViewFactory<T>
typealias ViewFactory<T> = (ViewGroup) -> T
