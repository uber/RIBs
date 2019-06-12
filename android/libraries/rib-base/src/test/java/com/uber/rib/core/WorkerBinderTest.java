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

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.uber.rib.core.lifecycle.InteractorEvent;
import com.uber.rib.core.lifecycle.WorkerEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.functions.Consumer;

import static com.uber.rib.core.WorkerBinder.bind;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WorkerBinderTest {

  @Mock private Worker worker;
  @Captor private ArgumentCaptor<WorkerScopeProvider> argumentCaptor;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void bind_whenInteractorAttached_shouldStartWorker() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    bind(lifecycle, worker);
    verify(worker).onStart(Matchers.<WorkerScopeProvider>any());
  }

  @Test
  public void bind_whenInteractorDetached_shouldStopWorker() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    bind(lifecycle, worker);
    lifecycle.accept(InteractorEvent.INACTIVE);
    verify(worker).onStop();
  }

  @Test
  public void unbind_whenInteractorAttached_shouldStopWorker() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    WorkerUnbinder unbinder = bind(lifecycle, worker);

    unbinder.unbind();

    verify(worker).onStop();
  }

  @Test
  public void unbind_whenOutsideInteractorLifecycle_shouldNotCallStopAgain() {
    BehaviorRelay<InteractorEvent> lifecycle =
        BehaviorRelay.createDefault(InteractorEvent.INACTIVE);
    WorkerUnbinder unbinder = bind(lifecycle, worker);

    verify(worker, times(1)).onStop();

    unbinder.unbind();

    verify(worker, times(1)).onStop();
  }

  @Test
  public void onInactive_whenAfterUnbind_shouldNotCallStopAgain() {
    BehaviorRelay<InteractorEvent> lifecycle = BehaviorRelay.createDefault(InteractorEvent.ACTIVE);
    WorkerUnbinder unbinder = bind(lifecycle, worker);

    unbinder.unbind();

    verify(worker, times(1)).onStop();

    lifecycle.accept(InteractorEvent.INACTIVE);

    verify(worker, times(1)).onStop();
  }
}
