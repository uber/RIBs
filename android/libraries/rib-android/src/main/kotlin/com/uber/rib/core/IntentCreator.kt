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

/** Creates intent objects. */
interface IntentCreator {
  /**
   * Create an explicit intent targeted at a particular class, which is guaranteed to be limited to
   * your app's package.
   *
   * @param cls The class that you intend to receive this intent.
   * @return The intent.
   */
  fun create(cls: Class<*>): Intent

  /**
   * Create an implicit intent targeted at an action, which may end up resolving to your app or to
   * any other app on the device which decides to look for this intent action. If you use this and
   * the intent is meant only for your app, it's wise to take additional precautions like setting
   * the package on the intent to your own app's package.
   *
   * @param action The intent action, which any app may register to receive.
   * @return The intent.
   */
  fun create(action: String): Intent
}
