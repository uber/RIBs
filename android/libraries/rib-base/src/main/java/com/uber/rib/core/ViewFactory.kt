package com.uber.rib.core

import android.view.ViewGroup

// this is only temporary, as I did not want to spend time with refactoring typealias to interface everywhere
interface ViewFactory<T>: (ViewGroup) -> T
