/*
 * Copyright (C) 2023. Uber Technologies
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

import com.uber.autodispose.coroutinesinterop.asScopeProvider
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

/** A manager or helper class bound to a [CoroutineScope] by using a binder like [bind]. */
public fun interface RibCoroutineWorker {
  /** Called when worker is started. Children coroutines can be launched in [scope]. */
  public suspend fun onStart(scope: CoroutineScope)

  /** Called when worker is stopped with [cause]. Should be fast, be non-blocking and not throw. */
  @JvmDefault public fun onStop(cause: Throwable) {}
}

// ---- Binder ---- //

/**
 * A handle to interact with [RibCoroutineWorker] binding job. This handle implements [Job], which
 * refers to the completion of [RibCoroutineWorker.onStart]. It can be [joined][join] to make sure
 * `onStart` finished. Note that children coroutines launched in the [CoroutineScope] passed on to
 * `onStart` are not waited: worker is considered bound when `onStart` finishes.
 */
public sealed interface BindWorkerHandle : Job {
  /** Unbinds the worker. */
  public fun unbind(): Job
}

private class BindWorkerHandleImpl(
  bindJob: Job,
  private val unbindJob: Job,
) : BindWorkerHandle, Job by bindJob {
  override fun unbind(): Job {
    unbindJob.cancel("Worker was manually unbound.")
    return unbindJob
  }
}

/**
 * Binds [worker] in a scope that is a child of the [CoroutineScope] receiver.
 *
 * The binding operation runs [RibCoroutineWorker.onStart] in a context inherited from the
 * [CoroutineScope] receiver, but with additional [context] elements that is, by default,
 * [RibDispatchers.Default]. This makes the worker run on the default dispatcher by default. Pass in
 * [EmptyCoroutineContext] instead if you want the worker to not override the dispatcher in the
 * scope (if any), or pass in a custom dispatcher as [context] to specify a different dispatcher. If
 * there is no dispatcher in [CoroutineScope] nor in [context], [RibDispatchers.Default] is used.
 *
 * The scope passed on to [RibCoroutineWorker.onStart] as a parameter is a child scope of the
 * [CoroutineScope] receiver, but with the additional [context] elements and a [SupervisorJob].
 *
 * Binding a worker is an asynchronous operation. To ensure [RibCoroutineWorker.onStart] is
 * finished, callers can [join][BindWorkerHandle.join] the resulting [BindWorkerHandle] when in a
 * coroutine:
 * ```
 * val handle = coroutineScope.bind(worker)
 * handle.join() // wait for onStart to finish
 * ```
 */
@JvmOverloads
public fun CoroutineScope.bind(
  worker: RibCoroutineWorker,
  context: CoroutineContext = RibDispatchers.Default,
): BindWorkerHandle {
  val bindJob: CompletableJob // A job that completes once worker's onStart completes
  val unbindJob =
    launch(context, { bindJob = createBindingJob() }) { bindAndAwaitCancellation(worker, bindJob) }
  return BindWorkerHandleImpl(bindJob, unbindJob)
}

/**
 * Guarantees to run synchronous [init] block exactly once in an undispatched manner.
 *
 * **Exceptions thrown in [init] block will be rethrown at call site.**
 */
@OptIn(ExperimentalContracts::class)
private fun CoroutineScope.launch(
  context: CoroutineContext = EmptyCoroutineContext,
  init: CoroutineScope.() -> Unit = {},
  block: suspend CoroutineScope.() -> Unit,
): Job {
  contract {
    callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }
  var initError: Throwable? = null
  val job =
    launch(context, CoroutineStart.UNDISPATCHED) {
      runCatching(init).onFailure { initError = it }.getOrThrow()
      dispatchIfNeeded()
      block()
    }
  initError?.let { throw it }
  return job
}

private suspend inline fun dispatchIfNeeded() {
  suspendCoroutineUninterceptedOrReturn sc@{ cont ->
    val context = cont.context
    val dispatcher = context[ContinuationInterceptor] as CoroutineDispatcher
    if (!dispatcher.isDispatchNeeded(context)) return@sc Unit
    // Coroutine was not in the right context -- we'll dispatch.
    context.ensureActive()
    cont.intercepted().resume(Unit)
    COROUTINE_SUSPENDED
  }
  // Don't continue if coroutine was cancelled after returning from dispatch.
  coroutineContext.ensureActive()
}

private fun CoroutineScope.createBindingJob(): CompletableJob =
  Job(coroutineContext.job).also {
    // Cancel `unbindJob` if `bindJob` has cancelled. This is important to abort `onStart` if
    // `bindJob` gets cancelled externally.
    // Note that in case of `bindJob` failure (e.g. `onStart` throws), `unbindJob` will
    // already fail by means of structured concurrency, but that does not happen on normal
    // cancellation. This `bindJob` cancellation upon the `unbindJob` cancellation is also
    // already set through structured concurrency.
    it.invokeOnCompletion { throwable -> if (throwable is CancellationException) cancel(throwable) }
  }

@Suppress("TooGenericExceptionCaught") // Exception is not swallowed
private suspend fun bindAndAwaitCancellation(worker: RibCoroutineWorker, bindJob: CompletableJob) {
  try {
    supervisorScope {
      worker.onStart(this)
      ensureActive()
      bindJob.complete()
      awaitCancellation() // Never returns normally, so we are sure an exception will be caught.
    }
  } catch (t: Throwable) {
    bindJob.cancelOrCompleteExceptionally(t)
    worker.onStop(t)
  }
}

/**
 * Cancel the deferred if [throwable] is a [CancellationException], otherwise completes it
 * exceptionally.
 */
private fun CompletableJob.cancelOrCompleteExceptionally(throwable: Throwable) {
  when (throwable) {
    is CancellationException -> cancel(throwable)
    else -> completeExceptionally(throwable)
  }
}

// ---- RibCoroutineWorker <-> Worker adapters ---- //

/** Converts a [Worker] to a [RibCoroutineWorker]. */
public fun Worker.asRibCoroutineWorker(): RibCoroutineWorker =
  WorkerToRibCoroutineWorkerAdapter(this)

/** Converts a [RibCoroutineWorker] to a [Worker]. */
@JvmOverloads
public fun RibCoroutineWorker.asWorker(
  coroutineContext: CoroutineContext = EmptyCoroutineContext,
): Worker = RibCoroutineWorkerToWorkerAdapter(this, coroutineContext)

internal open class WorkerToRibCoroutineWorkerAdapter(private val worker: Worker) :
  RibCoroutineWorker {
  override suspend fun onStart(scope: CoroutineScope) {
    withContext(worker.coroutineContext ?: EmptyCoroutineContext) {
      worker.onStart(scope.asWorkerScopeProvider())
    }
  }

  override fun onStop(cause: Throwable): Unit = worker.onStop()
}

internal open class RibCoroutineWorkerToWorkerAdapter
internal constructor(
  private val ribCoroutineWorker: RibCoroutineWorker,
  override val coroutineContext: CoroutineContext,
) : Worker {

  override fun onStart(lifecycle: WorkerScopeProvider) {
    // We can start it undispatched because Worker binder will already call `onStart` in correct
    // context,
    // but we still want to pass in `coroutineDispatcher` to resume from suspensions in `onStart` in
    // correct context.
    lifecycle.coroutineScope.launch(coroutineContext, start = CoroutineStart.UNDISPATCHED) {
      supervisorScope { ribCoroutineWorker.onStart(this) }
    }
  }

  override fun onStop() {
    ribCoroutineWorker.onStop(CancellationException("Worker is unbinding."))
  }
}

private fun CoroutineScope.asWorkerScopeProvider() = WorkerScopeProvider(asScopeProvider())
