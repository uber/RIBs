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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableJob
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
public fun interface RibCoroutineWorker : RibActionEmitter {

  /** Called when worker is started. Children coroutines can be launched in [scope]. */
  public suspend fun onStart(scope: CoroutineScope)

  /** Called when worker is stopped with [cause]. Should be fast, be non-blocking and not throw. */
  public fun onStop(cause: Throwable) {}
}

/** A manager or helper class bound to a [CoroutineScope] by using a binder like [bind]. */
public inline fun RibCoroutineWorker(
  crossinline onStart: suspend CoroutineScope.() -> Unit,
): RibCoroutineWorker {
  /*
   * 'RibCoroutineWorker' is already a functional interface; the purpose of this builder is to allow consumers
   * to create a 'RibCoroutineWorker' with 'CoroutineScope' in receiver position. E.g.
   *
   * Functional interface:
   * RibCoroutineWorker { scope ->
   *   scope.launch { ... }
   * }
   *
   * This factory method:
   * RibCoroutineWorker {
   *   launch { ... }
   * }
   */
  return RibCoroutineWorker { scope -> scope.onStart() }
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
  var bindJob: CompletableJob? = null // A job that completes once worker's onStart completes
  val unbindJob =
    launch(context, CoroutineStart.UNDISPATCHED) {
      val job = createBindingJob()
      bindJob = job
      // launch again -- this time, we will dispatch if installed dispatcher
      // tell us to (CoroutineDispatcher.isDispatchNeeded()).
      launch { bindAndAwaitCancellation(worker, job) }
    }
  // !! is safe here -- outer coroutine was started undispatched.
  return BindWorkerHandleImpl(bindJob!!, unbindJob)
}

/** Binds [workers] in a scope that is a child of the [CoroutineScope] receiver. */
@JvmOverloads
public fun CoroutineScope.bind(
  workers: Iterable<RibCoroutineWorker>,
  coroutineContext: CoroutineContext = RibDispatchers.Default,
) {
  for (worker in workers) {
    bind(worker, coroutineContext)
  }
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
      // In case no cancellation check was done at all in `onStart` (e.g. it did not suspend),
      // we want to cancel it before completing.
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

/**
 * Converts a [Worker] to a [RibCoroutineWorker].
 *
 * Returns the instance unchanged if it already implements [RibCoroutineWorker].
 */
public fun Worker.asRibCoroutineWorker(): RibCoroutineWorker =
  this as? RibCoroutineWorker ?: WorkerToRibCoroutineWorkerAdapter(this)

/**
 * Converts a [RibCoroutineWorker] to a [Worker].
 *
 * Returns the instance unchanged if it already implements [Worker]. In that case,
 * [coroutineContext] will not be used.
 */
@JvmOverloads
public fun RibCoroutineWorker.asWorker(
  coroutineContext: CoroutineContext = RibDispatchers.Default,
): Worker = this as? Worker ?: RibCoroutineWorkerToWorkerAdapter(this, coroutineContext)

internal open class WorkerToRibCoroutineWorkerAdapter(
  private val worker: Worker,
) : RibCoroutineWorker {
  override suspend fun onStart(scope: CoroutineScope) {
    withContext(worker.coroutineContext ?: EmptyCoroutineContext) {
      worker.onStart(scope.asWorkerScopeProvider())
    }
  }

  override fun onStop(cause: Throwable): Unit = worker.onStop()
}

internal open class RibCoroutineWorkerToWorkerAdapter(
  private val ribCoroutineWorker: RibCoroutineWorker,
  override val coroutineContext: CoroutineContext,
) : Worker {

  override fun onStart(lifecycle: WorkerScopeProvider) {
    // We start it undispatched to keep the behavior of immediate binding of Worker when
    // WorkerBinder.bind is called.
    // We still want to pass in `coroutineContext` to resume from suspensions in `onStart` in
    // correct context.
    lifecycle.coroutineScope.launch(coroutineContext, CoroutineStart.UNDISPATCHED) {
      supervisorScope {
        ribCoroutineWorker.onStart(this)
        // Keep this scope alive until cancelled.
        // This is particularly important for cases where we do not launch long-running coroutines
        // with scope, but instead install some completion handler that we expect to be called at
        // worker unbinding. This is the case with Rx subscriptions with 'autoDispose(scope)'
        awaitCancellation()
      }
    }
  }

  override fun onStop() {
    ribCoroutineWorker.onStop(CancellationException("Worker is unbinding."))
  }
}

private fun CoroutineScope.asWorkerScopeProvider() = WorkerScopeProvider(asScopeProvider())
