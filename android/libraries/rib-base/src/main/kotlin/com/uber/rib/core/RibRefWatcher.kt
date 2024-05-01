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

import androidx.annotation.MainThread

/**
 * Used to watch objects in order to verify they have no inbound references. Used to find memory
 * leaks.
 */
@MainThread
public open class RibRefWatcher {
  private var referenceWatcher: ReferenceWatcher? = null

  /**
   * Initialize Paper to use a [ReferenceWatcher] for observing deleted references.
   *
   * @param watcher the watcher.
   */
  public open fun setReferenceWatcher(watcher: ReferenceWatcher?) {
    referenceWatcher = watcher
  }

  /**
   * Watch this object to verify it has no inbound references.
   *
   * @param objectToWatch the object to watch.
   */
  public open fun watchDeletedObject(objectToWatch: Any?) {
    if (objectToWatch == null) {
      return
    }
    if (isLeakCanaryEnabled || uLeakEnabled) {
      referenceWatcher?.watch(objectToWatch)
    }
  }

  /**
   * Pipes breadcrumb data to the breadcrumb logger through the referenceWatcher.
   *
   * @param eventType Type of breadcrumb event
   * @param data breadcrumb data
   * @param parent breadcrumb parent if any
   */
  public open fun logBreadcrumb(eventType: String, child: String?, parent: String?) {
    if (referenceWatcher != null && breadcrumbsEnabled) {
      if (child == null || parent == null) {
        referenceWatcher?.logBreadcrumb(eventType, eventType, eventType)
      } else {
        referenceWatcher?.logBreadcrumb(eventType, child, parent)
      }
    }
  }

  /** Enables Breadcrumb logging. */
  public open fun enableBreadcrumbLogging() {
    breadcrumbsEnabled = true
  }

  /** Enables LeakCanary. */
  public open fun enableLeakCanary() {
    isLeakCanaryEnabled = true
  }

  /** Disables LeakCanary. */
  public open fun disableLeakCanary() {
    isLeakCanaryEnabled = false
  }

  /** Enables ULeak's Lifecycle tracking functionality. ULeak itself is behind a plugin. */
  public open fun enableULeakLifecycleTracking() {
    uLeakEnabled = true
  }

  /** Disables ULeak's Lifecycle Tracking. */
  public open fun disableULeakLifecycleTracking() {
    uLeakEnabled = false
  }

  /** Interface for classes that watch objects. */
  public interface ReferenceWatcher {
    /**
     * Watch this object to verify it has no inbound references.
     *
     * @param objectToWatch the object to watch.
     */
    public fun watch(objectToWatch: Any)

    /**
     * Method to pipe breadcrumbs into the Breadcrumb logger.
     *
     * @param eventType Type of Breadcrumb event
     * @param data The breadcrumb data
     * @param parent The breadcrumb parent
     */
    public fun logBreadcrumb(eventType: String, data: String, parent: String)
  }

  public companion object {
    private var ribRefWatcher: RibRefWatcher? = null

    /**
     * Get an instance of the [RibRefWatcher].
     *
     * @return the [RibRefWatcher] instance.
     */
    @JvmStatic
    public fun getInstance(): RibRefWatcher {
      if (ribRefWatcher == null) {
        ribRefWatcher = RibRefWatcher()
      }
      return ribRefWatcher!!
    }

    /**
     * Returns whether or not LeakCanary is enabled.
     *
     * @return whether or not LeakCanary is enabled.
     */
    @JvmStatic
    public var isLeakCanaryEnabled: Boolean = false
      private set
    private var uLeakEnabled = false
    private var breadcrumbsEnabled = false
  }
}
