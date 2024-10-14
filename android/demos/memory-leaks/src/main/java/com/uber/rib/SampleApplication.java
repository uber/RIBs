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
package com.uber.rib;

import android.app.Application;
import com.uber.rib.core.ActivityDelegate;
import com.uber.rib.core.HasActivityDelegate;
import com.uber.rib.core.RibRefWatcher;
import leakcanary.AppWatcher;

public class SampleApplication extends Application implements HasActivityDelegate {

  private SampleActivityDelegate activityDelegate;

  @Override
  public void onCreate() {
    activityDelegate = new SampleActivityDelegate();
    super.onCreate();
    installLeakCanary();
  }

  /** Install leak canary for both activities and RIBs. */
  private void installLeakCanary() {
    RibRefWatcher.getInstance()
        .setReferenceWatcher(
            new RibRefWatcher.ReferenceWatcher() {
              @Override
              public void watch(Object object, String description) {
                AppWatcher.INSTANCE.getObjectWatcher().expectWeaklyReachable(object, description);
              }

              @Override
              public void logBreadcrumb(String eventType, String data, String parent) {
                // Ignore for now. Useful for collecting production analytics.
              }
            });
    RibRefWatcher.getInstance().enableLeakCanary();
  }

  @Override
  public ActivityDelegate activityDelegate() {
    return activityDelegate;
  }

  private static final class SampleActivityDelegate implements ActivityDelegate {}
}
