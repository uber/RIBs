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

import android.os.Parcelable

/** Uber wrapper around Android Bundle to avoid Android and Robolectric dependencies. */
public open class Bundle
@JvmOverloads
constructor(
  private val androidBundle: android.os.Bundle = android.os.Bundle(),
) {

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired
   * type exists for the given key or if a null value is explicitly associated with the given key.
   *
   * @param key to fetch.
   * @param defaultValue if no value is present.
   * @return the boolean value associated with the given key or null if there is no string value in
   *   the bundle.
   */
  public open fun getBoolean(key: String, defaultValue: Boolean): Boolean {
    return androidBundle.getBoolean(key, defaultValue)
  }

  /**
   * Inserts a boolean value into the mapping of this Bundle, replacing any existing value for the
   * given key.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public open fun putBoolean(key: String, value: Boolean) {
    androidBundle.putBoolean(key, value)
  }

  /**
   * Returns a [Bundle] for a given key, or `null`.
   *
   * @param key to fetch.
   * @return a [Bundle] or `null`
   */
  public open fun getBundleExtra(key: String): Bundle? {
    val value = androidBundle.getParcelable<Parcelable>(key)
    return if (value != null) {
      Bundle(value as android.os.Bundle)
    } else {
      null
    }
  }

  /**
   * Inserts a wrapped Bundle value into the mapping of this Bundle, replacing any existing value
   * for the given key.
   *
   * @param key to insert.
   * @param bundle to insert.
   */
  public open fun putBundleExtra(key: String, bundle: Bundle?) {
    if (bundle != null) {
      androidBundle.putParcelable(key, bundle.androidBundle)
    } else {
      androidBundle.putParcelable(key, null)
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type
   * exists for the given key or a null value is explicitly associated with the key.
   *
   * @param key to get.
   * @return the value, or `null`.
   */
  public open fun getParcelable(key: String): Parcelable? {
    return androidBundle.getParcelable(key)
  }

  /**
   * Inserts a Parcelable value into the mapping of this Bundle, replacing any existing value for
   * the given key. Either key or value may be null.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public open fun putParcelable(key: String, value: Parcelable?) {
    androidBundle.putParcelable(key, value)
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired
   * type exists for the given key or if a null value is explicitly associated with the given key.
   *
   * @param key to fetch.
   * @return the String value associated with the given key or null if there is no string value in
   *   the bundle.
   */
  public open fun getString(key: String): String? {
    return androidBundle.getString(key)
  }

  /**
   * Inserts a String value into the mapping of this Bundle, replacing any existing value for the
   * given key.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public open fun putString(key: String, value: String?) {
    androidBundle.putString(key, value)
  }

  /**
   * Inserts an Int value into the mapping of this Bundle, replacing any existing value for the
   * given key.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public open fun putInt(key: String, value: Int) {
    androidBundle.putInt(key, value)
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired
   * type exists for the given key or if a null value is explicitly associated with the given key.
   *
   * @param key to fetch.
   * @return the int value associated with the given key or defaultValue if there is no int value in
   *   the bundle.
   */
  public open fun getInt(key: String, defaultValue: Int): Int {
    return androidBundle.getInt(key, defaultValue)
  }
}
