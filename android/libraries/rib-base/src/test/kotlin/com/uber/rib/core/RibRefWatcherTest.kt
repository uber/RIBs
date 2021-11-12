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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.After
import org.junit.Test

class RibRefWatcherTest {
  private val referenceWatcher: RibRefWatcher.ReferenceWatcher = mock()
  private val ribRefWatcher = RibRefWatcher()

  @After
  fun tearDown() {
    ribRefWatcher.disableLeakCanary()
    ribRefWatcher.disableULeakLifecycleTracking()
  }

  @Test
  fun watchDeletedObject_whenObjectIsNull_shouldDoNothing() {
    ribRefWatcher.enableLeakCanary()
    ribRefWatcher.setReferenceWatcher(referenceWatcher)
    ribRefWatcher.watchDeletedObject(null)
    verifyZeroInteractions(referenceWatcher)
  }

  @Test
  fun watchDeletedObject_whenReferenceWatcherIsNull_shouldDoNothing() {
    ribRefWatcher.enableLeakCanary()
    ribRefWatcher.watchDeletedObject(Any())
    verifyZeroInteractions(referenceWatcher)
  }

  @Test
  fun watchDeletedObject_whenReferenceObjectIsNotNull_shouldTellReferenceWatcher() {
    ribRefWatcher.enableLeakCanary()
    val obj = Any()
    ribRefWatcher.setReferenceWatcher(referenceWatcher)
    ribRefWatcher.watchDeletedObject(obj)
    verify(referenceWatcher).watch(obj)
  }

  @Test
  fun watchDeletedObject_whenNonNullRefWithDisabledLeakCanary_shouldDoNothing() {
    val obj = Any()
    ribRefWatcher.setReferenceWatcher(referenceWatcher)
    ribRefWatcher.watchDeletedObject(obj)
    verify(referenceWatcher, never()).watch(obj)
  }

  @Test
  fun watchDeletedObject_whenObjectIsNullWithULeak_shouldDoNothing() {
    ribRefWatcher.enableULeakLifecycleTracking()
    ribRefWatcher.setReferenceWatcher(referenceWatcher)
    ribRefWatcher.watchDeletedObject(null)
    verifyZeroInteractions(referenceWatcher)
  }

  @Test
  fun watchDeletedObject_whenReferenceWatcherIsNullULeakEnabled_shouldDoNothing() {
    ribRefWatcher.enableULeakLifecycleTracking()
    ribRefWatcher.watchDeletedObject(Any())
    verifyZeroInteractions(referenceWatcher)
  }

  @Test
  fun watchDeletedObject_whenReferenceObjectIsNotNullULeak_shouldTellReferenceWatcher() {
    ribRefWatcher.enableULeakLifecycleTracking()
    val obj = Any()
    ribRefWatcher.setReferenceWatcher(referenceWatcher)
    ribRefWatcher.watchDeletedObject(obj)
    verify(referenceWatcher).watch(obj)
  }

  @Test
  fun watchDeletedObject_whenNonNullRefULeakDisabled_shouldDoNothing() {
    val obj = Any()
    ribRefWatcher.setReferenceWatcher(referenceWatcher)
    ribRefWatcher.watchDeletedObject(obj)
    verify(referenceWatcher, never()).watch(obj)
  }
}
