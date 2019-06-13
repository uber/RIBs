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

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

/**
 * Used to watch objects in order to verify they have no inbound references. Used to find memory
 * leaks.
 */
@MainThread
public class RibRefWatcher {

  @Nullable private static RibRefWatcher instance;
  private static boolean leakCanaryEnabled = false;
  private static boolean uLeakEnabled = false;
  private static boolean breadcrumbsEnabled = false;
  @Nullable private ReferenceWatcher referenceWatcher;

  /**
   * Get an instance of the {@link RibRefWatcher}.
   *
   * @return the {@link RibRefWatcher} instance.
   */
  public static RibRefWatcher getInstance() {
    if (instance == null) {
      instance = new RibRefWatcher();
    }
    return instance;
  }

  /**
   * Initialize Paper to use a {@link ReferenceWatcher} for observing deleted references.
   *
   * @param watcher the watcher.
   */
  public void setReferenceWatcher(@Nullable ReferenceWatcher watcher) {
    referenceWatcher = watcher;
  }

  /**
   * Watch this object to verify it has no inbound references.
   *
   * @param object the object to watch.
   */
  public void watchDeletedObject(@Nullable Object object) {
    if (object == null) {
      return;
    }
    if (referenceWatcher != null && (leakCanaryEnabled || uLeakEnabled)) {
      referenceWatcher.watch(object);
    }
  }

  /**
   * Pipes breadcrumb data to the breadcrumb logger through the referenceWatcher.
   *
   * @param eventType Type of breadcrumb event
   * @param data breadcrumb data
   * @param parent breadcrumb parent if any
   */
  void logBreadcrumb(
      final String eventType, @Nullable final String child, @Nullable final String parent) {
    if (referenceWatcher != null && breadcrumbsEnabled) {
      if (child == null || parent == null) {
        referenceWatcher.logBreadcrumb(eventType, eventType, eventType);
      } else {
        referenceWatcher.logBreadcrumb(eventType, child, parent);
      }
    }
  }

  /** Enables Breadcrumb logging. */
  public void enableBreadcrumbLogging() {
    breadcrumbsEnabled = true;
  }

  /** Enables LeakCanary. */
  public void enableLeakCanary() {
    leakCanaryEnabled = true;
  }

  /** Disables LeakCanary. */
  public void disableLeakCanary() {
    leakCanaryEnabled = false;
  }

  /**
   * Returns whether or not LeakCanary is enabled.
   *
   * @return whether or not LeakCanary is enabled.
   */
  public static boolean isLeakCanaryEnabled() {
    return leakCanaryEnabled;
  }

  /** Enables ULeak's Lifecycle tracking functionality. ULeak itself is behind a plugin. */
  public void enableULeakLifecycleTracking() {
    uLeakEnabled = true;
  }

  /** Disables ULeak's Lifecycle Tracking. */
  public void disableULeakLifecycleTracking() {
    uLeakEnabled = false;
  }

  /** Interface for classes that watch objects. */
  public interface ReferenceWatcher {
    /**
     * Watch this object to verify it has no inbound references.
     *
     * @param object the object to watch.
     */
    void watch(Object object);

    /**
     * Method to pipe breadcrumbs into the Breadcrumb logger.
     *
     * @param eventType Type of Breadcrumb event
     * @param data The breadcrumb data
     * @param parent The breadcrumb parent
     */
    void logBreadcrumb(String eventType, String data, String parent);
  }
}
