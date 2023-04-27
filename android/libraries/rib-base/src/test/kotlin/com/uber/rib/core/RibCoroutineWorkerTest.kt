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

import com.google.common.truth.Truth.assertThat
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

private const val ON_START_DELAY_DURATION_MILLIS = 100L

@OptIn(ExperimentalCoroutinesApi::class)
class RibCoroutineWorkerTest {
  @get:Rule val coroutineRule = RibCoroutinesRule()
  private val worker = TestRibCoroutineWorker()

  @Test
  fun bindWorkHandle_onJoin_thenJoinsBindingOperation() = runTest {
    val handle = bind(worker, UnconfinedTestDispatcher(testScheduler))
    assertThat(worker.onStartStarted).isTrue()
    assertThat(worker.onStartFinished).isFalse()
    handle.join()
    assertThat(worker.onStartFinished).isTrue()
    handle.unbind()
  }

  @Test
  fun bind_onErrorOnStartAndOnStop_propagatesExceptionWithSuppressed() = runTest {
    var throwable: Throwable? = null
    val ceh = CoroutineExceptionHandler { _, t -> throwable = t }
    withContext(ceh) {
      supervisorScope {
        val onStartErrorMsg = "onStart failure"
        val onStopErrorMsg = "onStop failure"
        worker.doOnStart { error(onStartErrorMsg) }
        worker.doOnStop { error(onStopErrorMsg) }
        bind(worker).join()
        assertThat(throwable).isInstanceOf(IllegalStateException::class.java)
        assertThat(throwable).hasMessageThat().isEqualTo(onStartErrorMsg)
        val suppressed = throwable?.suppressed?.firstOrNull()
        assertThat(suppressed).isInstanceOf(IllegalStateException::class.java)
        assertThat(suppressed).hasMessageThat().isEqualTo(onStopErrorMsg)
        assertThat(worker.onStartFinished).isTrue()
        assertThat(worker.onStopFinished).isTrue()
      }
    }
  }

  @Test(expected = IllegalStateException::class)
  fun bindWorkHandle_onError_thenPropagatesException() = runTest {
    worker.doOnStart { error("onStart failure") }
    bind(worker)
  }

  @Test
  fun unbindHandler_onJoin_thenJoinsUnbindOperation() = runTest {
    val bindHandle = bind(worker)
    bindHandle.join()
    val unbindHandle = bindHandle.unbind()
    assertThat(worker.onStopFinished).isFalse()
    unbindHandle.join()
    assertThat(worker.onStopFinished).isTrue()
    assertThat(worker.onStopCause).isInstanceOf(CancellationException::class.java)
    assertThat(worker.onStopCause).hasMessageThat().isEqualTo("Worker was manually unbound.")
  }

  @Test
  fun onScopeCancelledAfterBinding_workerIsUnboundAutomatically() = runTest {
    val cancellationMsg = "Scope cancelled"
    launch {
      bind(worker, EmptyCoroutineContext).join()
      cancel(cancellationMsg)
    }
    advanceUntilIdle()
    assertThat(worker.onStopFinished).isTrue()
    assertThat(worker.onStopCause).isInstanceOf(CancellationException::class.java)
    assertThat(worker.onStopCause).hasMessageThat().isEqualTo(cancellationMsg)
  }

  @Test
  fun onScopeCancelledBeforeBinding_parentScopeCanCompleteNormally() = runTest {
    launch {
      bind(worker)
      // bind was called, but coroutine that runs onStart is not started yet.
      cancel("Scope cancelling")
    }
    // if test fails to finish, there are unfinished jobs.
  }

  @Test
  fun onBindingJobCancelledBeforeBinding_parentScopeCanCompleteNormally() = runTest {
    launch { bind(worker).cancel("Cancelling bind work") }
    // if test fails to finish, there are unfinished jobs.
  }

  @Test
  fun onScopeCancelledOnWorkerOnStart_parentScopeCanCompleteNormally() = runTest {
    val cancellationMsg = "Cancelling on onStart"
    worker.doOnStart { currentCoroutineContext().cancel(CancellationException(cancellationMsg)) }
    bind(worker).join()
  }

  @OptIn(DelicateCoroutinesApi::class)
  @Test
  fun onBindingWithCustomDispatcher_dispatchesToCustomDispatcher() = runTest {
    val callingContext = newSingleThreadContext("Calling context")
    val executionContext = newSingleThreadContext("Execution context")
    executionContext.use { execCtx ->
      callingContext.use { callingCtx ->
        withContext(callingCtx) {
          val handle = bind(worker, execCtx)
          handle.join()
          assertThat(worker.onStartThread!!.name).startsWith("Execution context")
          handle.unbind().join()
          assertThat(worker.onStopThread!!.name).startsWith("Execution context")
        }
      }
    }
  }

  @Test
  fun onBindingOnCorrectContext_doNotPayForDispatch() = runTest {
    val dispatcher = ImmediateDispatcher(testScheduler)
    dispatcher {
      dispatcher.setThreadId()
      assertThat(dispatcher.dispatchCount).isEqualTo(1)
      val handle = bind(worker, EmptyCoroutineContext)
      assertThat(dispatcher.dispatchCount).isEqualTo(1) // no new dispatch done
      assertThat(worker.onStartStarted).isTrue() // started undispatched
      assertThat(worker.onStartThread!!.id).isEqualTo(Thread.currentThread().id)
      // run delay on onStart
      advanceTimeBy(ON_START_DELAY_DURATION_MILLIS)
      runCurrent()
      assertThat(worker.onStartFinished).isTrue()
      assertThat(dispatcher.dispatchCount).isEqualTo(1) // no new dispatch done
      handle.unbind()
      assertThat(worker.onStopThread!!.id).isEqualTo(Thread.currentThread().id)
      assertThat(worker.onStopFinished).isTrue()
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
private class ImmediateDispatcher(
  scheduler: TestCoroutineScheduler,
  private val delegate: TestDispatcher = StandardTestDispatcher(scheduler),
) : CoroutineDispatcher(), Delay by delegate {
  private var threadId: Long? = null
  var dispatchCount = 0
    private set

  override fun isDispatchNeeded(context: CoroutineContext): Boolean {
    val expectedThreadId = threadId ?: return true
    return Thread.currentThread().id != expectedThreadId
  }

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    ++dispatchCount
    delegate.dispatch(context, block)
  }

  fun setThreadId() {
    threadId = Thread.currentThread().id
  }
}

private class TestRibCoroutineWorker : RibCoroutineWorker {
  var onStartStarted = false
  var onStartFinished = false
  var onStartThread: Thread? = null
  var onStopCause: Throwable? = null
  var onStopFinished = false
  var onStopThread: Thread? = null

  private var _doOnStart: suspend () -> Unit = {}
  private var _doOnStop: () -> Unit = {}

  fun doOnStart(block: suspend () -> Unit) {
    _doOnStart = block
  }

  fun doOnStop(block: () -> Unit) {
    _doOnStop = block
  }

  override suspend fun onStart(scope: CoroutineScope) {
    onStartStarted = true
    onStartThread = Thread.currentThread()
    try {
      scope.launch { awaitCancellation() }
      delay(ON_START_DELAY_DURATION_MILLIS)
      _doOnStart()
    } finally {
      onStartFinished = true
    }
  }

  override fun onStop(cause: Throwable) {
    onStopThread = Thread.currentThread()
    onStopCause = cause
    try {
      _doOnStop()
    } finally {
      onStopFinished = true
    }
  }
}
