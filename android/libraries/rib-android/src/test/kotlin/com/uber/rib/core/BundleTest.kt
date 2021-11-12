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

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BundleTest {

  private lateinit var androidBundle: android.os.Bundle
  private lateinit var bundle: Bundle

  @Before
  fun setup() {
    androidBundle = android.os.Bundle()
    androidBundle.putString(TEST_KEY_STRING, TEST_VALUE_STRING)
    bundle = Bundle(androidBundle)
  }

  @Test
  fun string_shouldReturnValueFromAndroidBundle() {
    assertThat(bundle.getString(TEST_KEY_STRING)).isEqualTo(TEST_VALUE_STRING)
  }

  @Test
  fun putString_shouldSetValueOnAndroidBundle() {
    val newValue = "test"
    bundle.putString(TEST_KEY_STRING, newValue)
    assertThat(androidBundle.getString(TEST_KEY_STRING)).isEqualTo(newValue)
  }

  companion object {
    private const val TEST_KEY_STRING = "test_string_key"
    private const val TEST_VALUE_STRING = "test_string_value"
  }
}
