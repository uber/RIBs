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

/**
 * Start activities. A much cleaner dependency than an entire activity or context, and easier to
 * inject and mock in tests.
 */
interface ActivityStarter {
  /**
   * Start an activity with the given intent.
   *
   * @param intent The intent to open a new activity.
   */
  fun startActivity(intent: Intent)

  /**
   * Start an activity with the given intent, to be notified when that activity finishes.
   *
   * @param intent The intent to open a new activity.
   * @param requestCode The code unique to your current activity to know which activity result is
   *   from this request.
   */
  @Deprecated("""use plain Activity instead""")
  fun startActivityForResult(intent: Intent, requestCode: Int)
}
