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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

open class RibDebugOverlay : Drawable() {
  private var enabled = true

  open fun setEnabled(enabled: Boolean) {
    this.enabled = enabled
  }

  override fun draw(canvas: Canvas) {
    if (enabled) {
      val p = Paint()
      p.color = OVERLAY_COLOR
      p.alpha = OVERLAY_ALPHA
      p.style = Paint.Style.FILL
      canvas.drawPaint(p)
    }
  }

  override fun setAlpha(i: Int) {}

  override fun setColorFilter(colorFilter: ColorFilter?) {}

  override fun getOpacity() = PixelFormat.TRANSLUCENT

  companion object {
    private const val OVERLAY_COLOR = Color.RED
    private const val OVERLAY_ALPHA = 35
  }
}
