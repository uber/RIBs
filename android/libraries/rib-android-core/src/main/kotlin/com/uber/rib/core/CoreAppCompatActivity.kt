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

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity

/** Core Support v7 AppCompat Activity. */
abstract class CoreAppCompatActivity : AppCompatActivity() {

  private var activityDelegate: ActivityDelegate? = null

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    if (applicationContext is HasActivityDelegate) {
      activityDelegate = (applicationContext as HasActivityDelegate).activityDelegate()
    }
    super.onCreate(savedInstanceState)
    activityDelegate?.onCreate(savedInstanceState)
  }

  @CallSuper
  override fun onStart() {
    super.onStart()
    activityDelegate?.onStart()
  }

  @CallSuper
  override fun onResume() {
    super.onResume()
    activityDelegate?.onResume()
  }

  @CallSuper
  override fun onPause() {
    activityDelegate?.onPause()
    super.onPause()
  }

  @CallSuper
  override fun onStop() {
    activityDelegate?.onStop()
    super.onStop()
  }

  @CallSuper
  override fun onDestroy() {
    activityDelegate?.onDestroy()
    activityDelegate = null
    super.onDestroy()
  }

  @CallSuper
  override fun onRequestPermissionsResult(
    @IntRange(from = 0, to = 255) requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    activityDelegate?.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
  }

  @CallSuper
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    activityDelegate?.onActivityResult(this, requestCode, resultCode, data)
  }
}
