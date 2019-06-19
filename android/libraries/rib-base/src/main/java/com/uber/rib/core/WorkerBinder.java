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

import androidx.annotation.VisibleForTesting;

import com.jakewharton.rxrelay2.PublishRelay;
import com.uber.rib.core.lifecycle.InteractorEvent;
import com.uber.rib.core.lifecycle.WorkerEvent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
    return bind(interactor.lifecycle(), worker);
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

  @VisibleForTesting
  static WorkerUnbinder bind(Observable<InteractorEvent> lifecycle, final Worker worker) {
    final PublishRelay<WorkerEvent> unbindSubject = PublishRelay.create();

    final Observable<WorkerEvent> workerLifecycle =
        lifecycle
            .map(
                new Function<InteractorEvent, WorkerEvent>() {
                  @Override
                  public WorkerEvent apply(InteractorEvent interactorEvent) {
                    switch (interactorEvent) {
                      case ACTIVE:
                        return WorkerEvent.START;
                      default:
                        return WorkerEvent.STOP;
                    }
                  }
                })
            .mergeWith(unbindSubject)
            .takeUntil(
                new Predicate<WorkerEvent>() {
                  @Override
                  public boolean test(WorkerEvent workerEvent) throws Exception {
                    return workerEvent == WorkerEvent.STOP;
                  }
                });

    workerLifecycle.subscribe(new Consumer<WorkerEvent>() {
      @Override
      public void accept(WorkerEvent workerEvent) throws Exception {
        switch (workerEvent) {
          case START:
            worker.onStart(new WorkerScopeProvider(workerLifecycle.hide()));
            break;
          default:
            worker.onStop();
            break;
        }
      }
    });

    return new WorkerUnbinder() {
      @Override
      public void unbind() {
        unbindSubject.accept(WorkerEvent.STOP);
      }
    };
  }
}
