package com.uber.rib.root

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Top level view for {@link RootBuilder.RootScope}.
 */
class RootView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle), RootInteractor.RootPresenter
