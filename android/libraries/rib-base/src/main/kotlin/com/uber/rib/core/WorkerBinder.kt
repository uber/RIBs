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

import androidx.annotation.VisibleForTesting;
import com.jakewharton.rxrelay2.PublishRelay;
import com.uber.autodispose.lifecycle.LifecycleScopeProvider;
import com.uber.rib.core.lifecycle.InteractorEvent;
import com.uber.rib.core.lifecycle.PresenterEvent;
import com.uber.rib.core.lifecycle.WorkerEvent;
import io.reactivex.Observable;
import java.util.List;

/**
 * Helper class to bind to an interactor's lifecycle to translate it to a {@link Worker} lifecycle.
 */
public final class WorkerBinder {

  private WorkerBinder() {}

  /**
   * Bind a worker (ie. a manager or any other class that needs an interactor's lifecycle) to an
   * interactor's lifecycle events. Inject this class into your interactor and call this method on
   * any
   *
   * @param interactor The interactor that provides the lifecycle.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @return {@link WorkerUnbinder} to unbind {@link Worker Worker's} lifecycle.
   */
  public static WorkerUnbinder bind(Interactor<?, ?> interactor, Worker worker) {
    return bind(mapInteractorLifecycleToWorker(interactor.lifecycle()), worker);
  }

  /**
   * Bind a list of workers (ie. a manager or any other class that needs an interactor's lifecycle)
   * to an interactor's lifecycle events. Use this class into your interactor and call this method
   * on attach.
   *
   * @param interactor The interactor that provides the lifecycle.
   * @param workers A list of classes that want to be informed when to start and stop doing work.
   */
  public static void bind(Interactor interactor, List<? extends Worker> workers) {
    for (Worker interactorWorker : workers) {
      bind(interactor, interactorWorker);
    }
  }

  /**
   * Bind a worker (ie. a manager or any other class that needs an presenter's lifecycle) to an
   * presenter's lifecycle events. Inject this class into your presenter and call this method on any
   *
   * @param presenter The presenter that provides the lifecycle.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @return {@link WorkerUnbinder} to unbind {@link Worker Worker's} lifecycle.
   */
  public static WorkerUnbinder bind(Presenter presenter, Worker worker) {
    return bind(mapPresenterLifecycleToWorker(presenter.lifecycle()), worker);
  }

  /**
   * Bind a list of workers (ie. a manager or any other class that needs an presenter's lifecycle)
   * to an presenter's lifecycle events. Use this class into your presenter and call this method on
   * attach.
   *
   * @param presenter The presenter that provides the lifecycle.
   * @param workers A list of classes that want to be informed when to start and stop doing work.
   */
  public static void bind(Presenter presenter, List<? extends Worker> workers) {
    for (Worker worker : workers) {
      bind(presenter, worker);
    }
  }

  @VisibleForTesting
  static WorkerUnbinder bind(Observable<WorkerEvent> mappedLifecycle, final Worker worker) {
    final PublishRelay<WorkerEvent> unbindSubject = PublishRelay.create();

    final Observable<WorkerEvent> workerLifecycle =
        mappedLifecycle
            .mergeWith(unbindSubject)
            .takeUntil(workerEvent -> workerEvent == WorkerEvent.STOP);

    bindToWorkerLifecycle(workerLifecycle, worker);

    return () -> unbindSubject.accept(WorkerEvent.STOP);
  }

  static Observable<WorkerEvent> mapInteractorLifecycleToWorker(
      Observable<InteractorEvent> interactorEventObservable) {
    return interactorEventObservable.map(
        interactorEvent -> {
          switch (interactorEvent) {
            case ACTIVE:
              return WorkerEvent.START;
            default:
              return WorkerEvent.STOP;
          }
        });
  }

  static Observable<WorkerEvent> mapPresenterLifecycleToWorker(
      Observable<PresenterEvent> presenterEventObservable) {
    return presenterEventObservable.map(
        presenterEvent -> {
          switch (presenterEvent) {
            case LOADED:
              return WorkerEvent.START;
            default:
              return WorkerEvent.STOP;
          }
        });
  }

  /**
   * Bind a worker (ie. a manager or any other class that needs an interactor's lifecycle) to an
   * interactor's lifecycle events.
   *
   * @param lifecycle The interactor's {@link LifecycleScopeProvider}.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @deprecated this method uses {@code LifecycleScopeProvider} for purposes other than
   *     AutoDispose. Usage is strongly discouraged as this method may be removed in the future.
   */
  @Deprecated
  public static void bindTo(
      LifecycleScopeProvider<InteractorEvent> lifecycle, final Worker worker) {
    bind(mapInteractorLifecycleToWorker(lifecycle.lifecycle()), worker);
  }

  /**
   * Bind a worker to a {@link WorkerEvent} lifecycle provider.
   *
   * @param workerLifecycle the worker lifecycle event provider
   * @param worker the class that wants to be informed when to start and stop doing work
   */
  @SuppressWarnings("CheckReturnValue")
  public static void bindToWorkerLifecycle(
      Observable<WorkerEvent> workerLifecycle, final Worker worker) {
    workerLifecycle.subscribe(
        workerEvent -> {
          switch (workerEvent) {
            case START:
              worker.onStart(new WorkerScopeProvider(workerLifecycle.hide()));
              break;
            default:
              worker.onStop();
              break;
          }
        });
  }
}
