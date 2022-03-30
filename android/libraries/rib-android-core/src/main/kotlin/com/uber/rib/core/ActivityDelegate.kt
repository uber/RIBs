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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IntRange

/**
 * This class represents a delegate which you can use to extend [CoreAppCompatActivity]'s
 * functionality. This allows [RibActivity] and any other type of [Activity] that you need to
 * support to share [CoreAppCompatActivity] as a common parent.
 */
interface ActivityDelegate {
  /** @see [Activity.onCreate] */
  @JvmDefault
  fun onCreate(savedInstanceState: Bundle?) {}

  /** @see [Activity.onStart] */
  @JvmDefault
  fun onStart() {}

  /** @see [Activity.onResume] */
  @JvmDefault
  fun onResume() {}

  /** @see [Activity.onPause] */
  @JvmDefault
  fun onPause() {}

  /** @see [Activity.onStop] */
  @JvmDefault
  fun onStop() {}

  /** @see [Activity.onDestroy] */
  @JvmDefault
  fun onDestroy() {}

  /** @see [Activity.onActivityResult] */
  @JvmDefault
  fun onActivityResult(
    activity: Activity,
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {}

  /** @see [Activity.onRequestPermissionsResult] */
  @JvmDefault
  fun onRequestPermissionsResult(
    activity: Activity,
    @IntRange(from = 0, to = 255) requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {}
}
