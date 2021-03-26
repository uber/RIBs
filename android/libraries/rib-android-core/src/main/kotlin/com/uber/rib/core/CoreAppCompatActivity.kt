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

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/** Core Support v7 AppCompat Activity. */
public abstract class CoreAppCompatActivity extends AppCompatActivity {

  @Nullable private ActivityDelegate activityDelegate;

  @CallSuper
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    if (getApplicationContext() instanceof HasActivityDelegate) {
      activityDelegate = ((HasActivityDelegate) getApplicationContext()).activityDelegate();
    }
    super.onCreate(savedInstanceState);
    if (activityDelegate != null) {
      activityDelegate.onCreate(savedInstanceState);
    }
  }

  @CallSuper
  @Override
  protected void onStart() {
    super.onStart();
    if (activityDelegate != null) {
      activityDelegate.onStart();
    }
  }

  @CallSuper
  @Override
  protected void onResume() {
    super.onResume();
    if (activityDelegate != null) {
      activityDelegate.onResume();
    }
  }

  @CallSuper
  @Override
  protected void onPause() {
    if (activityDelegate != null) {
      activityDelegate.onPause();
    }
    super.onPause();
  }

  @CallSuper
  @Override
  protected void onStop() {
    if (activityDelegate != null) {
      activityDelegate.onStop();
    }
    super.onStop();
  }

  @CallSuper
  @Override
  protected void onDestroy() {
    if (activityDelegate != null) {
      activityDelegate.onDestroy();
      activityDelegate = null;
    }
    super.onDestroy();
  }

  @CallSuper
  @Override
  public void onRequestPermissionsResult(
      @IntRange(from = 0, to = 255) int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (activityDelegate != null) {
      activityDelegate.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
  }

  @CallSuper
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (activityDelegate != null) {
      activityDelegate.onActivityResult(this, requestCode, resultCode, data);
    }
  }
}
