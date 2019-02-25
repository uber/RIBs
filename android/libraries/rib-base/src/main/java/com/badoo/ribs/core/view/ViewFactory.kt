package com.badoo.ribs.core.view

import android.view.ViewGroup

interface ViewFactory<T>: (ViewGroup) -> T
