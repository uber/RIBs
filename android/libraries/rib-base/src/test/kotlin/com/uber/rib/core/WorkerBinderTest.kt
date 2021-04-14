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
package com.uber.rib.core;

import static com.uber.rib.core.WorkerBinder.bind;
import static com.uber.rib.core.WorkerBinder.bindToWorkerLifecycle;
import static com.uber.rib.core.WorkerBinder.mapInteractorLifecycleToWorker;
import static com.uber.rib.core.WorkerBinder.mapPresenterLifecycleToWorker;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.uber.rib.core.lifecycle.InteractorEvent;
import com.uber.rib.core.lifecycle.PresenterEvent;
import com.uber.rib.core.lifecycle.WorkerEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WorkerBinderTest {

  @Mock private Worker worker;

  @SuppressWarnings("NullAway.Init")
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void bind_whenInteractorAttached_shouldStartWorker() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    bind(mapInteractorLifecycleToWorker(lifecycle), worker);
    verify(worker).onStart(Matchers.<WorkerScopeProvider>any());
  }

  @Test
  public void bind_whenInteractorDetached_shouldStopWorker() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    bind(mapInteractorLifecycleToWorker(lifecycle), worker);
    lifecycle.accept(InteractorEvent.INACTIVE);
    verify(worker).onStop();
  }

  @Test
  public void unbind_whenInteractorAttached_shouldStopWorker() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    WorkerUnbinder unbinder = bind(mapInteractorLifecycleToWorker(lifecycle), worker);

    unbinder.unbind();

    verify(worker).onStop();
  }

  @Test
  public void unbind_whenOutsideInteractorLifecycle_shouldNotCallStopAgain() {
    BehaviorRelay<InteractorEvent> lifecycle =
        BehaviorRelay.createDefault(InteractorEvent.INACTIVE);
    WorkerUnbinder unbinder = bind(mapInteractorLifecycleToWorker(lifecycle), worker);

    verify(worker, times(1)).onStop();

    unbinder.unbind();

    verify(worker, times(1)).onStop();
  }

  @Test
  public void onInactive_whenAfterUnbind_shouldNotCallStopAgain() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    WorkerUnbinder unbinder = bind(mapInteractorLifecycleToWorker(lifecycle), worker);

    unbinder.unbind();

    verify(worker, times(1)).onStop();

    lifecycle.accept(InteractorEvent.INACTIVE);

    verify(worker, times(1)).onStop();
  }

  @Test
  public void bindToWorkerLifecycle_whenStartEventEmitted_shouldStartWorker() {
    BehaviorRelay<WorkerEvent> lifecycle = BehaviorRelay.createDefault(WorkerEvent.START);
    bindToWorkerLifecycle(lifecycle, worker);
    verify(worker).onStart(Matchers.any());
  }

  @Test
  public void bindToWorkerLifecycle_whenStopEventEmitted_shouldStopWorker() {
    BehaviorRelay<WorkerEvent> lifecycle = BehaviorRelay.createDefault(WorkerEvent.START);
    bindToWorkerLifecycle(lifecycle, worker);
    lifecycle.accept(WorkerEvent.STOP);
    verify(worker).onStop();
  }

  @Test
  public void bind_whenPresenterAttached_shouldStartWorker() {
    BehaviorRelay<PresenterEvent> lifecycle = BehaviorRelay.createDefault(PresenterEvent.LOADED);
    bind(mapPresenterLifecycleToWorker(lifecycle), worker);
    verify(worker).onStart(any());
  }

  @Test
  public void bind_whenPresenterDetached_shouldStopWorker() {
    BehaviorRelay<PresenterEvent> lifecycle = BehaviorRelay.createDefault(PresenterEvent.LOADED);
    bind(mapPresenterLifecycleToWorker(lifecycle), worker);
    lifecycle.accept(PresenterEvent.UNLOADED);
    verify(worker).onStop();
  }

  @Test
  public void unbind_whenPresenterAttached_shouldStopWorker() {
    BehaviorRelay<PresenterEvent> lifecycle = BehaviorRelay.createDefault(PresenterEvent.LOADED);
    WorkerUnbinder unbinder = bind(mapPresenterLifecycleToWorker(lifecycle), worker);

    unbinder.unbind();

    verify(worker).onStop();
  }

  @Test
  public void unbind_whenOutsidePresenterLifecycle_shouldNotCallStopAgain() {
    BehaviorRelay<PresenterEvent> lifecycle = BehaviorRelay.createDefault(PresenterEvent.UNLOADED);
    WorkerUnbinder unbinder = bind(mapPresenterLifecycleToWorker(lifecycle), worker);

    verify(worker, times(1)).onStop();

    unbinder.unbind();

    verify(worker, times(1)).onStop();
  }
}
