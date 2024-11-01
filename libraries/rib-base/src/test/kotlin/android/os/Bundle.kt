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
package android.os

/** Stub class to have pure Java unit tests. */
class Bundle : Parcelable {
  private val testData: MutableMap<String, Any> = mutableMapOf()

  fun getString(key: String): String? {
    return testData[key] as String?
  }

  fun <T : Parcelable?> getParcelable(key: String): T? {
    return testData[key] as T?
  }

  fun putParcelable(key: String, value: Parcelable) {
    testData[key] = value
  }

  fun putString(key: String, value: String) {
    testData[key] = value
  }
}
