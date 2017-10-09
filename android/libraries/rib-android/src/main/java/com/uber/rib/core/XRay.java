/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

/** Utility class that shows riblets name in its background. */
public final class XRay {

  private static final XRay INSTANCE = new XRay();

  private static final int FRAME_WIDTH = 500;
  private static final int FRAME_HEIGHT = 150;
  private static final float XRAY_ALFA = 0.9f;
  private static final int TEXT_SIZE = 30;
  private static final int TEXT_COLOR = Color.RED;

  private boolean isEnabled;

  private final Paint textPaint;

  private XRay() {
    isEnabled = false;

    textPaint = new Paint();
    textPaint.setTextSize(TEXT_SIZE);
    textPaint.setColor(TEXT_COLOR);
  }

  /** Toggles state of XRay. */
  public static void toggle() {
    INSTANCE.isEnabled = !INSTANCE.isEnabled;
  }

  /** @return {@code true} if XRay is enabled, {@code false} otherwise. */
  public static boolean isEnabled() {
    return INSTANCE.isEnabled;
  }

  /**
   * Puts {@link ViewBuilder}s riblet name in the background of the {@link View}
   *
   * @param viewBuilder a {@link ViewBuilder} which riblets name should be written.
   * @param view a {@link View} to put the name behind.
   */
  static void apply(final ViewBuilder viewBuilder, final View view) {
    final Drawable oldBackground = view.getBackground();

    final Bitmap bitmap;

    if (oldBackground != null) {
      bitmap = drawableToBitmap(oldBackground);
    } else {
      bitmap = Bitmap.createBitmap(FRAME_WIDTH, FRAME_HEIGHT, Bitmap.Config.ARGB_8888);
    }

    INSTANCE.writeOnBitmap(bitmap, getRibletName(viewBuilder));

    final BitmapDrawable newBackground =
        new BitmapDrawable(view.getContext().getResources(), bitmap);
    newBackground.setGravity(Gravity.CENTER);

    view.setBackground(newBackground);
    view.setAlpha(XRAY_ALFA);
  }

  private static Bitmap drawableToBitmap(final Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      final BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
      if (bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }

    final Bitmap bitmap;
    if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
      bitmap = Bitmap.createBitmap(FRAME_WIDTH, FRAME_HEIGHT, Bitmap.Config.ARGB_8888);
    } else {
      bitmap =
          Bitmap.createBitmap(
              drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }

    final Canvas canvas = new Canvas(bitmap);
    drawable.draw(canvas);
    return bitmap;
  }

  private static String getRibletName(final ViewBuilder viewBuilder) {
    return viewBuilder.getClass().getSimpleName().replace("Builder", "");
  }

  private void writeOnBitmap(final Bitmap bitmap, final String text) {
    final Canvas canvas = new Canvas(bitmap);

    final float xStartPoint = (bitmap.getWidth() - textPaint.measureText(text)) / 2f;
    final float yStartPoint = bitmap.getHeight() / 2f;

    canvas.drawText(text, xStartPoint, yStartPoint, textPaint);
  }
}
