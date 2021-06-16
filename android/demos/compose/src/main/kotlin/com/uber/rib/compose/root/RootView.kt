package com.uber.rib.compose.root

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

class RootView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

  init {
    setBackgroundColor(Color.RED)
    addView(TextView(context).apply {
      text = "root (view)"
      setTextColor(Color.WHITE)
    })
  }
}
