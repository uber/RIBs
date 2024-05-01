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
import com.uber.debug.broadcast.core.DebugBroadcastReceiver;
import com.uber.debug.broadcast.rib.RibHierarchyDebugBroadcastHandler;
import com.uber.rib.core.RibEvents;
import com.uber.rib.intellij.BuildConfig;
import java.util.Arrays;

public class SampleApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable IntelliJ RIB tree plugin extension
    if (BuildConfig.DEBUG) {
      DebugBroadcastReceiver.initWithDefaults(
          this,
          Arrays.asList(
              new RibHierarchyDebugBroadcastHandler(
                  getApplicationContext(), RibEvents.getRouterEvents())));
    }
  }
}
