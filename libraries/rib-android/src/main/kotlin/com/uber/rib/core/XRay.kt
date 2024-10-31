/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View

/** Utility class that shows riblets name in its background. */
class XRay private constructor() {
  private var isEnabled = false
  private var textPaint: Paint? = null
  private fun writeOnBitmap(bitmap: Bitmap, text: String) {
    val canvas = Canvas(bitmap)
    val textPaint = getTextPaint()
    val xStartPoint = (bitmap.width - textPaint.measureText(text)) / 2f
    val yStartPoint = bitmap.height / 2f
    canvas.drawText(text, xStartPoint, yStartPoint, textPaint)
  }

  private fun getTextPaint(): Paint {
    if (textPaint == null) {
      textPaint =
        Paint().apply {
          textSize = TEXT_SIZE.toFloat()
          color = TEXT_COLOR
        }
    }
    return textPaint!!
  }

  companion object {
    private val INSTANCE = XRay()
    private const val FRAME_WIDTH = 500
    private const val FRAME_HEIGHT = 150
    private const val XRAY_ALFA = 0.9f
    private const val TEXT_SIZE = 30
    private const val TEXT_COLOR = Color.RED

    /** Toggles state of XRay. */
    @JvmStatic
    fun toggle() {
      INSTANCE.isEnabled = !INSTANCE.isEnabled
    }

    /** @return `true` if XRay is enabled, `false` otherwise. */
    @JvmStatic
    fun isEnabled(): Boolean {
      return INSTANCE.isEnabled
    }

    /**
     * Puts [ViewBuilder]s riblet name in the background of the [View]
     *
     * @param viewRouter a [ViewRouter] which riblets name should be written.
     * @param view a [View] to put the name behind.
     */
    @JvmStatic
    fun apply(viewRouter: ViewRouter<*, *>, view: View) {
      val oldBackground = view.background
      val bitmap: Bitmap =
        if (oldBackground != null) {
          drawableToBitmap(oldBackground)
        } else {
          Bitmap.createBitmap(FRAME_WIDTH, FRAME_HEIGHT, Bitmap.Config.ARGB_8888)
        }
      INSTANCE.writeOnBitmap(bitmap, getRibletName(viewRouter))
      val newBackground = BitmapDrawable(view.context.resources, bitmap)
      newBackground.gravity = Gravity.CENTER
      view.background = newBackground
      view.alpha = XRAY_ALFA
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
      if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
          return drawable.bitmap
        }
      }
      val bitmap: Bitmap =
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
          Bitmap.createBitmap(FRAME_WIDTH, FRAME_HEIGHT, Bitmap.Config.ARGB_8888)
        } else {
          Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888,
          )
        }
      val canvas = Canvas(bitmap)
      drawable.draw(canvas)
      return bitmap
    }

    private fun getRibletName(viewRouter: ViewRouter<*, *>): String {
      return viewRouter.javaClass.simpleName.replace("Router", "")
    }
  }
}
