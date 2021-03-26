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
package com.uber.rib.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

/**
 * This class represents a delegate which you can use to extend CoreAppCompatActivity's
 * functionality. This allows RibActivity and any other type of Activity that you need to support to
 * share CoreAppCompatActivity as a common parent.
 */
public interface ActivityDelegate {
  /** @see {@link Activity#onCreate(Bundle) } */
  default void onCreate(@Nullable Bundle savedInstanceState) {}

  /** @see {@link Activity#onStart() } */
  default void onStart() {}

  /** @see {@link Activity#onResume() } */
  default void onResume() {}

  /** @see {@link Activity#onPause() } */
  default void onPause() {}

  /** @see {@link Activity#onStop() } */
  default void onStop() {}

  /** @see {@link Activity#onDestroy() } */
  default void onDestroy() {}

  /** @see {@link Activity#onActivityResult(Activity, int, int, Intent) } */
  default void onActivityResult(
      Activity activity, int requestCode, int resultCode, @Nullable Intent data) {}

  /** @see {@link Activity#onRequestPermissionsResult(Activity, int, String[], int[]) } */
  default void onRequestPermissionsResult(
      Activity activity,
      @IntRange(from = 0, to = 255) int requestCode,
      String[] permissions,
      int[] grantResults) {}
}
