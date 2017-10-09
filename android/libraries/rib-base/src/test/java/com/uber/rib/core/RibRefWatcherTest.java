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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class RibRefWatcherTest {

  @Mock private RibRefWatcher.ReferenceWatcher referenceWatcher;

  private final RibRefWatcher ribRefWatcher = new com.uber.rib.core.RibRefWatcher();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    ribRefWatcher.disableLeakCanary();
    ribRefWatcher.disableULeakLifecycleTracking();
  }

  @Test
  public void watchDeletedObject_whenObjectIsNull_shouldDoNothing() {
    ribRefWatcher.enableLeakCanary();
    ribRefWatcher.setReferenceWatcher(referenceWatcher);
    ribRefWatcher.watchDeletedObject(null);
    verifyZeroInteractions(referenceWatcher);
  }

  @Test
  public void watchDeletedObject_whenReferenceWatcherIsNull_shouldDoNothing() {
    ribRefWatcher.enableLeakCanary();
    ribRefWatcher.watchDeletedObject(new Object());
    verifyZeroInteractions(referenceWatcher);
  }

  @Test
  public void watchDeletedObject_whenReferenceObjectIsNotNull_shouldTellReferenceWatcher() {
    ribRefWatcher.enableLeakCanary();
    Object object = new Object();
    ribRefWatcher.setReferenceWatcher(referenceWatcher);
    ribRefWatcher.watchDeletedObject(object);
    verify(referenceWatcher).watch(object);
  }

  @Test
  public void watchDeletedObject_whenNonNullRefWithDisabledLeakCanary_shouldDoNothing() {
    Object object = new Object();
    ribRefWatcher.setReferenceWatcher(referenceWatcher);
    ribRefWatcher.watchDeletedObject(object);
    verify(referenceWatcher, never()).watch(object);
  }

  @Test
  public void watchDeletedObject_whenObjectIsNullWithULeak_shouldDoNothing() {
    ribRefWatcher.enableULeakLifecycleTracking();
    ribRefWatcher.setReferenceWatcher(referenceWatcher);
    ribRefWatcher.watchDeletedObject(null);
    verifyZeroInteractions(referenceWatcher);
  }

  @Test
  public void watchDeletedObject_whenReferenceWatcherIsNullULeakEnabled_shouldDoNothing() {
    ribRefWatcher.enableULeakLifecycleTracking();
    ribRefWatcher.watchDeletedObject(new Object());
    verifyZeroInteractions(referenceWatcher);
  }

  @Test
  public void watchDeletedObject_whenReferenceObjectIsNotNullULeak_shouldTellReferenceWatcher() {
    ribRefWatcher.enableULeakLifecycleTracking();
    Object object = new Object();
    ribRefWatcher.setReferenceWatcher(referenceWatcher);
    ribRefWatcher.watchDeletedObject(object);
    verify(referenceWatcher).watch(object);
  }

  @Test
  public void watchDeletedObject_whenNonNullRefULeakDisabled_shouldDoNothing() {
    Object object = new Object();
    ribRefWatcher.setReferenceWatcher(referenceWatcher);
    ribRefWatcher.watchDeletedObject(object);
    verify(referenceWatcher, never()).watch(object);
  }
}
