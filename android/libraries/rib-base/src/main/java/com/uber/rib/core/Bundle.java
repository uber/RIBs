/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core;

import android.os.Parcelable;
import androidx.annotation.Nullable;

/** Uber wrapper around Android Bundle to avoid Android and Robolectric dependencies. */
public class Bundle {

  private final android.os.Bundle androidBundle;

  public Bundle() {
    this(null);
  }

  /**
   * Creates a new Uber bundle that wraps an Android bundle.
   *
   * @param androidBundle Andorid bundle to convert.
   */
  public Bundle(@Nullable android.os.Bundle androidBundle) {
    if (androidBundle == null) {
      this.androidBundle = new android.os.Bundle();
    } else {
      this.androidBundle = androidBundle;
    }
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired
   * type exists for the given key or if a null value is explicitly associated with the given key.
   *
   * @param key to fetch.
   * @param defaultValue if no value is present.
   * @return the boolean value associated with the given key or null if there is no string value in
   *     the bundle.
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    return androidBundle.getBoolean(key, defaultValue);
  }

  /**
   * Inserts a boolean value into the mapping of this Bundle, replacing any existing value for the
   * given key.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public void putBoolean(String key, boolean value) {
    androidBundle.putBoolean(key, value);
  }

  /**
   * Returns a {@link Bundle} for a given key, or {@code null}.
   *
   * @param key to fetch.
   * @return a {@link Bundle} or {@code null}
   */
  @Nullable
  public Bundle getBundleExtra(String key) {
    Parcelable value = androidBundle.getParcelable(key);
    if (value != null) {
      return new Bundle((android.os.Bundle) value);
    } else {
      return null;
    }
  }

  /**
   * Inserts a wrapped Bundle value into the mapping of this Bundle, replacing any existing value
   * for the given key.
   *
   * @param key to insert.
   * @param bundle to insert.
   */
  public void putBundleExtra(String key, @Nullable Bundle bundle) {
    if (bundle != null) {
      androidBundle.putParcelable(key, bundle.getWrappedBundle());
    } else {
      androidBundle.putParcelable(key, null);
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type
   * exists for the given key or a null value is explicitly associated with the key.
   *
   * @param key to get.
   * @return the value, or {@code null}.
   */
  @Nullable
  public Parcelable getParcelable(String key) {
    return androidBundle.getParcelable(key);
  }

  /**
   * Inserts a Parcelable value into the mapping of this Bundle, replacing any existing value for
   * the given key. Either key or value may be null.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public void putParcelable(String key, @Nullable Parcelable value) {
    androidBundle.putParcelable(key, value);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired
   * type exists for the given key or if a null value is explicitly associated with the given key.
   *
   * @param key to fetch.
   * @return the String value associated with the given key or null if there is no string value in
   *     the bundle.
   */
  @Nullable
  public String getString(String key) {
    return androidBundle.getString(key);
  }

  /**
   * Inserts a String value into the mapping of this Bundle, replacing any existing value for the
   * given key.
   *
   * @param key to insert.
   * @param value to insert.
   */
  public void putString(String key, @Nullable String value) {
    androidBundle.putString(key, value);
  }

  android.os.Bundle getWrappedBundle() {
    return androidBundle;
  }
}
