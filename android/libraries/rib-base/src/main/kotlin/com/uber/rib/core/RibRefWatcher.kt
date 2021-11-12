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
open class RibRefWatcher {
  private var referenceWatcher: ReferenceWatcher? = null

  /**
   * Initialize Paper to use a [ReferenceWatcher] for observing deleted references.
   *
   * @param watcher the watcher.
   */
  open fun setReferenceWatcher(watcher: ReferenceWatcher?) {
    referenceWatcher = watcher
  }

  /**
   * Watch this object to verify it has no inbound references.
   *
   * @param objectToWatch the object to watch.
   */
  open fun watchDeletedObject(objectToWatch: Any?) {
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
  open fun logBreadcrumb(eventType: String, child: String?, parent: String?) {
    if (referenceWatcher != null && breadcrumbsEnabled) {
      if (child == null || parent == null) {
        referenceWatcher?.logBreadcrumb(eventType, eventType, eventType)
      } else {
        referenceWatcher?.logBreadcrumb(eventType, child, parent)
      }
    }
  }

  /** Enables Breadcrumb logging.  */
  open fun enableBreadcrumbLogging() {
    breadcrumbsEnabled = true
  }

  /** Enables LeakCanary.  */
  open fun enableLeakCanary() {
    isLeakCanaryEnabled = true
  }

  /** Disables LeakCanary.  */
  open fun disableLeakCanary() {
    isLeakCanaryEnabled = false
  }

  /** Enables ULeak's Lifecycle tracking functionality. ULeak itself is behind a plugin.  */
  open fun enableULeakLifecycleTracking() {
    uLeakEnabled = true
  }

  /** Disables ULeak's Lifecycle Tracking.  */
  open fun disableULeakLifecycleTracking() {
    uLeakEnabled = false
  }

  /** Interface for classes that watch objects.  */
  interface ReferenceWatcher {
    /**
     * Watch this object to verify it has no inbound references.
     *
     * @param object the object to watch.
     */
    fun watch(objectToWatch: Any)

    /**
     * Method to pipe breadcrumbs into the Breadcrumb logger.
     *
     * @param eventType Type of Breadcrumb event
     * @param data The breadcrumb data
     * @param parent The breadcrumb parent
     */
    fun logBreadcrumb(eventType: String, data: String, parent: String)
  }

  companion object {
    private var ribRefWatcher: RibRefWatcher? = null

    /**
     * Get an instance of the [RibRefWatcher].
     *
     * @return the [RibRefWatcher] instance.
     */
    @JvmStatic
    fun getInstance(): RibRefWatcher {
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
    var isLeakCanaryEnabled = false
      private set
    private var uLeakEnabled = false
    private var breadcrumbsEnabled = false
  }
}
