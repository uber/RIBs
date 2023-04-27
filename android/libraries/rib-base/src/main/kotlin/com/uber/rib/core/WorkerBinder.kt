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
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.PresenterEvent
import com.uber.rib.core.lifecycle.WorkerEvent
import io.reactivex.Observable
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

/** Helper class to bind to an interactor's lifecycle to translate it to a [Worker] lifecycle. */
public object WorkerBinder {

  private var workerBinderListenerWeakRef: WeakReference<WorkerBinderListener>? = null

  /**
   * Initializes reporting of [WorkerBinderInfo] via [WorkerBinderListener]
   *
   * IMPORTANT: This should be called only once at early app scope to get proper monitoring of early
   * worker being bound
   */
  @JvmStatic
  public fun initializeMonitoring(workerBinderListener: WorkerBinderListener) {
    this.workerBinderListenerWeakRef = WeakReference<WorkerBinderListener>(workerBinderListener)
  }

  /**
   * Bind a worker (ie. a manager or any other class that needs an interactor's lifecycle) to an
   * interactor's lifecycle events. Inject this class into your interactor and call this method on
   * any
   *
   * @param interactor The interactor that provides the lifecycle.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @param binderCoroutineDispatcher CoroutineDispatcher to be apply only when
   *   [Worker.coroutineContext] is not overriden with a value different that
   *   [EmptyCoroutineContext]
   * @return [WorkerUnbinder] to unbind [Worker]'s lifecycle.
   */
  @JvmStatic
  @JvmOverloads
  public fun bind(
    interactor: Interactor<*, *>,
    worker: Worker,
    binderCoroutineDispatcher: CoroutineDispatcher = RibDispatchers.Unconfined,
  ): WorkerUnbinder =
    worker.bind(
      interactor.lifecycleFlow,
      Interactor.lifecycleRange,
      binderCoroutineDispatcher,
      workerBinderListenerWeakRef
    )

  /**
   * Bind a list of workers (ie. a manager or any other class that needs an interactor's lifecycle)
   * to an interactor's lifecycle events. Use this class into your interactor and call this method
   * on attach.
   *
   * @param interactor The interactor that provides the lifecycle.
   * @param workers A list of classes that want to be informed when to start and stop doing work.
   * @param binderCoroutineDispatcher CoroutineDispatcher to be apply only when
   *   [Worker.coroutineContext] is not overriden with a value different that
   *   [EmptyCoroutineContext] [RibDispatchers.Unconfined]
   */
  @JvmStatic
  @JvmOverloads
  public fun bind(
    interactor: Interactor<*, *>,
    workers: List<Worker>,
    dispatcherAtBinder: CoroutineDispatcher = RibDispatchers.Unconfined,
  ) {
    for (interactorWorker in workers) {
      bind(interactor, interactorWorker, dispatcherAtBinder)
    }
  }

  /**
   * Bind a worker (ie. a manager or any other class that needs an presenter's lifecycle) to an
   * presenter's lifecycle events. Inject this class into your presenter and call this method on any
   *
   * @param presenter The presenter that provides the lifecycle.
   * @param worker The class that wants to be informed when to start and stop doing work.
   * @param binderCoroutineDispatcher CoroutineDispatcher to be apply only when
   *   [Worker.coroutineContext] is not overriden with a value different that
   *   [EmptyCoroutineContext] [RibDispatchers.Unconfined]
   * @return [WorkerUnbinder] to unbind [Worker]'s lifecycle.
   */
  @JvmStatic
  @JvmOverloads
  public fun bind(
    presenter: Presenter,
    worker: Worker,
    dispatcherAtBinder: CoroutineDispatcher = RibDispatchers.Unconfined,
  ): WorkerUnbinder =
    worker.bind(
      presenter.lifecycleFlow,
      Presenter.lifecycleRange,
      dispatcherAtBinder,
      workerBinderListenerWeakRef
    )

  /**
   * Bind a list of workers (ie. a manager or any other class that needs an presenter's lifecycle)
   * to an presenter's lifecycle events. Use this class into your presenter and call this method on
   * attach.
   *
   * @param presenter The presenter that provides the lifecycle.
   * @param workers A list of classes that want to be informed when to start and stop doing work.
   * @param dispatcherAtBinder Optional dispatcher to force binding of a list of all Workers (unless
   *   there is a [Worker] in the list with [coroutineDispatcher] other than
   *   [RibDispatchers.Unconfined]
   */
  @JvmStatic
  public fun bind(
    presenter: Presenter,
    workers: List<Worker>,
    dispatcherAtBinder: CoroutineDispatcher = RibDispatchers.Unconfined,
  ) {
    for (worker in workers) {
      bind(presenter, worker, dispatcherAtBinder)
    }
  }

  @JvmStatic
  @VisibleForTesting
  @Deprecated(
    message =
      """
      This method doesn't support the [WorkerBinderThreadingType] defined at Worker. Due to this, the binding
      will happen on the caller thread without a possibility to change the threading (even specifying a different WorkerThreadingType other than CALLER_THREAD)
      It also doesn't provide information for WorkerBinderDuration when a [WorkerDurationMonitoringListener] is added
    """,
    replaceWith = ReplaceWith("bind(interactor, worker) or bind(presenter, worker)"),
  )
  public fun bind(mappedLifecycle: Observable<WorkerEvent>, worker: Worker): WorkerUnbinder {
    val disposable =
      mappedLifecycle
        .takeWhile { it != WorkerEvent.STOP }
        .doFinally { worker.onStop() }
        .subscribe { worker.onStart(WorkerScopeProvider(mappedLifecycle)) }
    return WorkerUnbinder(disposable::dispose)
  }

  @JvmStatic
  public fun mapInteractorLifecycleToWorker(
    interactorEventObservable: Observable<InteractorEvent>,
  ): Observable<WorkerEvent> {
    return interactorEventObservable.map { interactorEvent: InteractorEvent ->
      when (interactorEvent) {
        InteractorEvent.ACTIVE -> return@map WorkerEvent.START
        else -> return@map WorkerEvent.STOP
      }
    }
  }

  @JvmStatic
  public fun mapPresenterLifecycleToWorker(
    presenterEventObservable: Observable<PresenterEvent>,
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
        AutoDispose. Usage is strongly discouraged as this method may be removed in the future.""",
  )
  public fun bindTo(
    lifecycle: LifecycleScopeProvider<InteractorEvent>,
    worker: Worker,
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
  @Deprecated(
    message =
      """
      This method is unsafe because it assumes that the 'workerLifecycle' Observable completes after it
      emits 'WorkerEvent.STOP'. If it does not complete, the subscription will leak.
    """,
    replaceWith = ReplaceWith("bind(workerLifecycle, worker)"),
  )
  public fun bindToWorkerLifecycle(
    workerLifecycle: Observable<WorkerEvent>,
    worker: Worker,
  ) {
    workerLifecycle.subscribe { workerEvent: WorkerEvent ->
      when (workerEvent) {
        WorkerEvent.START -> worker.onStart(WorkerScopeProvider(workerLifecycle.hide()))
        else -> worker.onStop()
      }
    }
  }
}

/**
 * Holds relevant information for [dispatcherAtBinder] [workerCoroutineWorker] and resulting
 * [jobCoroutineWorker]
 */
public data class CoroutineContextInfo(
  val dispatcherAtBinder: CoroutineDispatcher,
  val workerCoroutineWorker: CoroutineContext,
  val jobCoroutineWorker: CoroutineContext,
)

/**
 * Holds all relevant information for completed Worker.onStart/onStop actions. (e.g. Name of the
 * Worker bound, duration of total onStart/onStop, thread name where onStart/onStop happens,etc)
 */
public data class WorkerBinderInfo(
  /** Worker class name */
  val workerName: String,

  /** Worker event type (START/STOP) */
  val workerEvent: WorkerEvent,

  /** [CoroutineContextInfo] contains all relevant WorkerBinding CoroutineContext information */
  val coroutineContextInfo: CoroutineContextInfo,

  /**
   * Thread name where Worker.onStart/onStop was called.
   *
   * e.g. When [CoroutineDispatcher] is set as [RibDispatchers.Default] a sample threadName value
   * would be similar to `DefaultDispatcher-worker-2`
   */
  val threadName: String,

  /** Total binding duration in milliseconds of Worker.onStart/onStop */
  val totalBindingDurationMilli: Long,
)

/** Reports total binding duration of Worker.onStart/onStop */
public fun interface WorkerBinderListener {

  /**
   * Reports all related Worker information via [WorkerBinderInfo] when onStart/onStop methods are
   * completed
   */
  public fun onBindCompleted(
    workerBinderInfo: WorkerBinderInfo,
  )
}

private fun getCoroutineContextInfo(
  dispatcherAtBinder: CoroutineDispatcher,
  workerCoroutineContext: CoroutineContext,
): CoroutineContextInfo {
  val jobCoroutineContext =
    if (workerCoroutineContext != EmptyCoroutineContext) {
      workerCoroutineContext
    } else {
      dispatcherAtBinder
    }
  return CoroutineContextInfo(dispatcherAtBinder, workerCoroutineContext, jobCoroutineContext)
}

private fun <T : Comparable<T>> Worker.bind(
  lifecycle: SharedFlow<T>,
  lifecycleRange: ClosedRange<T>,
  binderCoroutineDispatcher: CoroutineDispatcher = RibDispatchers.Unconfined,
  workerDurationListenerWeakRef: WeakReference<WorkerBinderListener>?,
): WorkerUnbinder {
  val coroutineContextInfo =
    getCoroutineContextInfo(
      binderCoroutineDispatcher,
      workerCoroutineContext = this.coroutineContext,
    )
  val coroutineStart =
    if (coroutineContextInfo.jobCoroutineWorker == RibDispatchers.Unconfined) {
      CoroutineStart.UNDISPATCHED
    } else {
      CoroutineStart.DEFAULT
    }

  /*
   * We need `Dispatchers.Unconfined` to react immediately to lifecycle flow emissions, and we need
   * `CoroutineStart.Undispatched` to prevent coroutines launched in `onStart` with `Dispatchers.Unconfined`
   * from forming an event loop instead of starting eagerly.
   *
   * GlobalScope won't leak the job, because the flow completes when lifecycle completes.
   */
  @OptIn(DelicateCoroutinesApi::class)
  val job =
    GlobalScope.launch(
      coroutineContextInfo.jobCoroutineWorker,
      start = coroutineStart,
    ) {
      lifecycle
        .takeWhile { it < lifecycleRange.endInclusive }
        .onCompletion {
          bindAndReportWorkerInfo(
            workerDurationListenerWeakRef,
            WorkerEvent.STOP,
            coroutineContextInfo
          ) {
            onStop()
          }
        }
        .collect {
          bindAndReportWorkerInfo(
            workerDurationListenerWeakRef,
            WorkerEvent.START,
            coroutineContextInfo,
          ) {
            val workerScopeProvider = WorkerScopeProvider(lifecycle.asScopeProvider(lifecycleRange))
            onStart(workerScopeProvider)
          }
        }
    }
  return WorkerUnbinder(job::cancel)
}

private inline fun Worker.bindAndReportWorkerInfo(
  workerBinderListeners: WeakReference<WorkerBinderListener>?,
  event: WorkerEvent,
  coroutineContextInfo: CoroutineContextInfo,
  workerBinderAction: Worker.() -> Unit,
) {
  val duration = measureTimeMillis { workerBinderAction() }
  workerBinderListeners?.reportWorkerBinderInfo(this, coroutineContextInfo, event, duration)
}

private fun WeakReference<WorkerBinderListener>.reportWorkerBinderInfo(
  worker: Worker,
  coroutineContextInfo: CoroutineContextInfo,
  workerEvent: WorkerEvent,
  totalBindingEventMilli: Long,
) {
  val workerClassName = worker.javaClass.name
  val currentThreadName = Thread.currentThread().name

  val workerBinderInfo =
    WorkerBinderInfo(
      workerClassName,
      workerEvent,
      coroutineContextInfo,
      currentThreadName,
      totalBindingEventMilli,
    )

  this@reportWorkerBinderInfo.get()?.onBindCompleted(workerBinderInfo)
}
