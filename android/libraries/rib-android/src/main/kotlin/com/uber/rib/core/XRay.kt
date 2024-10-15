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
import androidx.annotation.VisibleForTesting

/** Utility class that shows riblets name in its background. */
public object XRay {
  private var config = XRayConfig()
  private val textPaint =
    Paint().apply {
      textSize = TEXT_SIZE.toFloat()
      color = TEXT_COLOR
    }

  private fun writeOnBitmap(bitmap: Bitmap, text: String) {
    Canvas(bitmap).run {
      val xStartPoint = (bitmap.width - textPaint.measureText(text)) / 2f
      val yStartPoint = bitmap.height / 2f
      drawText(text, xStartPoint, yStartPoint, textPaint)
    }
  }

  /** Setup XRay using a [XRayConfig] */
  @JvmStatic
  public fun setup(config: XRayConfig) {
    this.config = config
  }

  /** Toggles state of XRay. */
  @Deprecated(
    message = "toggle() may lead to switch-on-switch-off behavior. Use setup() instead.",
    replaceWith = ReplaceWith("setup(XRayConfig(enabled = !config.enabled))"),
  )
  @JvmStatic
  public fun toggle() {
    setup(config.copy(enabled = !config.enabled))
  }

  /** @return `true` if XRay is enabled, `false` otherwise. */
  @JvmStatic
  public fun isEnabled(): Boolean {
    return this.config.enabled
  }

  /**
   * Puts [ViewBuilder]s riblet name in the background of the [View]
   *
   * @param routerName the riblets name to be written.
   * @param view a [View] to put the name behind.
   */
  @JvmStatic
  internal fun apply(routerName: String, view: View) {
    val oldBackground = view.background
    val bitmap: Bitmap =
      if (oldBackground != null) {
        drawableToBitmap(oldBackground)
      } else {
        Bitmap.createBitmap(FRAME_WIDTH, FRAME_HEIGHT, Bitmap.Config.ARGB_8888)
      }
    writeOnBitmap(bitmap, getShortRibletName(routerName))
    view.background =
      BitmapDrawable(view.context.resources, bitmap).apply { gravity = Gravity.CENTER }

    if (config.alphaEnabled) {
      view.alpha = XRAY_ALPHA
    }
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

  @VisibleForTesting
  internal fun getShortRibletName(originalName: String): String {
    return if (originalName != "Router") {
      originalName.replace("Router", "")
    } else {
      originalName
    }
  }

  private const val FRAME_WIDTH = 500
  private const val FRAME_HEIGHT = 150
  private const val XRAY_ALPHA = 0.9f
  private const val TEXT_SIZE = 30
  private const val TEXT_COLOR = Color.RED
}

public data class XRayConfig(
  val enabled: Boolean = false,
  val alphaEnabled: Boolean = true,
)
