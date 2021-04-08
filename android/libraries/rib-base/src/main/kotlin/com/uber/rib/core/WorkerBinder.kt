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

import androidx.annotation.VisibleForTesting
import com.jakewharton.rxrelay2.PublishRelay
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.PresenterEvent
import com.uber.rib.core.lifecycle.WorkerEvent
import io.reactivex.Observable

/** Helper class to bind to an interactor's lifecycle to translate it to a [Worker] lifecycle. */
object WorkerBinder {
  /**
   * Bind a worker (ie. a manager or any other class that needs an interactor's lifecycle) to an
   * interactor's lifecycle events. Inject this class into your interactor and call this method on
   * any
   *
   * @param interactor The interactor that provides the lifecycle.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @return [WorkerUnbinder] to unbind [Worker&#39;s][Worker] lifecycle.
   */
  @JvmStatic
  open fun bind(interactor: Interactor<*, *>, worker: Worker): WorkerUnbinder {
    return bind(mapInteractorLifecycleToWorker(interactor.lifecycle()), worker)
  }

  /**
   * Bind a list of workers (ie. a manager or any other class that needs an interactor's lifecycle)
   * to an interactor's lifecycle events. Use this class into your interactor and call this method
   * on attach.
   *
   * @param interactor The interactor that provides the lifecycle.
   * @param workers A list of classes that want to be informed when to start and stop doing work.
   */
  @JvmStatic
  open fun bind(interactor: Interactor<*, *>, workers: List<Worker>) {
    for (interactorWorker in workers) {
      bind(interactor, interactorWorker)
    }
  }

  /**
   * Bind a worker (ie. a manager or any other class that needs an presenter's lifecycle) to an
   * presenter's lifecycle events. Inject this class into your presenter and call this method on any
   *
   * @param presenter The presenter that provides the lifecycle.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @return [WorkerUnbinder] to unbind [Worker&#39;s][Worker] lifecycle.
   */
  @JvmStatic
  open fun bind(presenter: Presenter, worker: Worker): WorkerUnbinder {
    return bind(mapPresenterLifecycleToWorker(presenter.lifecycle()), worker)
  }

  /**
   * Bind a list of workers (ie. a manager or any other class that needs an presenter's lifecycle)
   * to an presenter's lifecycle events. Use this class into your presenter and call this method on
   * attach.
   *
   * @param presenter The presenter that provides the lifecycle.
   * @param workers A list of classes that want to be informed when to start and stop doing work.
   */
  @JvmStatic
  open fun bind(presenter: Presenter, workers: List<Worker>) {
    for (worker in workers) {
      bind(presenter, worker)
    }
  }

  @JvmStatic
  @VisibleForTesting
  open fun bind(mappedLifecycle: Observable<WorkerEvent>, worker: Worker): WorkerUnbinder {
    val unbindSubject = PublishRelay.create<WorkerEvent>()
    val workerLifecycle = mappedLifecycle
      .mergeWith(unbindSubject)
      .takeUntil { workerEvent: WorkerEvent -> workerEvent === WorkerEvent.STOP }
    bindToWorkerLifecycle(workerLifecycle, worker)
    return WorkerUnbinder { unbindSubject.accept(WorkerEvent.STOP) }
  }

  @JvmStatic
  fun mapInteractorLifecycleToWorker(
    interactorEventObservable: Observable<InteractorEvent>
  ): Observable<WorkerEvent> {
    return interactorEventObservable.map { interactorEvent: InteractorEvent ->
      when (interactorEvent) {
        InteractorEvent.ACTIVE -> return@map WorkerEvent.START
        else -> return@map WorkerEvent.STOP
      }
    }
  }

  @JvmStatic
  fun mapPresenterLifecycleToWorker(
    presenterEventObservable: Observable<PresenterEvent>
  ): Observable<WorkerEvent> {
    return presenterEventObservable.map { presenterEvent: PresenterEvent ->
      when (presenterEvent) {
        PresenterEvent.LOADED -> return@map WorkerEvent.START
        else -> return@map WorkerEvent.STOP
      }
    }
  }

  /**
   * Bind a worker (ie. a manager or any other class that needs an interactor's lifecycle) to an
   * interactor's lifecycle events.
   *
   * @param lifecycle The interactor's [LifecycleScopeProvider].
   * @param worker The class that wants to be informed when to start and stop doing work.
   */
  @JvmStatic
  @Deprecated(
    """this method uses {@code LifecycleScopeProvider} for purposes other than
        AutoDispose. Usage is strongly discouraged as this method may be removed in the future."""
  )
  open fun bindTo(
    lifecycle: LifecycleScopeProvider<InteractorEvent>,
    worker: Worker
  ) {
    bind(mapInteractorLifecycleToWorker(lifecycle.lifecycle()), worker)
  }

  /**
   * Bind a worker to a [WorkerEvent] lifecycle provider.
   *
   * @param workerLifecycle the worker lifecycle event provider
   * @param worker the class that wants to be informed when to start and stop doing work
   */
  @JvmStatic
  fun bindToWorkerLifecycle(
    workerLifecycle: Observable<WorkerEvent>,
    worker: Worker
  ) {
    workerLifecycle.subscribe { workerEvent: WorkerEvent ->
      when (workerEvent) {
        WorkerEvent.START -> worker.onStart(WorkerScopeProvider(workerLifecycle.hide()))
        else -> worker.onStop()
      }
    }
  }
}
