package com.uber.rib.core

import android.view.ViewGroup

interface ViewFactory<T>: (ViewGroup) -> T
