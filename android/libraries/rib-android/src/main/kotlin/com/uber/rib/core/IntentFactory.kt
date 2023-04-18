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

/** Factory for an [Intent] that opens an activity. */
interface IntentFactory {
  /**
   * Create a view router to be displayed for an [Intent].
   *
   * @param intentCreator to create the [Intent].
   * @return the activity [Intent].
   */
  fun create(intentCreator: IntentCreator): Intent
}
