/*
 * Copyright (C) 2022. Uber Technologies
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

import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.MainScope

public object RibDispatchers : RibDispatchersProvider {
  override val Default: CoroutineDispatcher
    get() = RibCoroutinesConfig.dispatchers.Default

  override val Main: MainCoroutineDispatcher
    get() = RibCoroutinesConfig.dispatchers.Main

  override val IO: CoroutineDispatcher
    get() = RibCoroutinesConfig.dispatchers.IO

  override val Unconfined: CoroutineDispatcher
    get() = RibCoroutinesConfig.dispatchers.Unconfined
}

public data class DefaultRibDispatchers(
  override val Default: CoroutineDispatcher = Dispatchers.Default,
  override val Main: MainCoroutineDispatcher = Dispatchers.Main,
  override val IO: CoroutineDispatcher = Dispatchers.IO,
  override val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined,
) : RibDispatchersProvider

/** Allows providing default Dispatchers used for Rib CoroutineScopes */
@Suppress("PropertyName")
public interface RibDispatchersProvider {

  /**
   * The Default [CoroutineDispatcher] that behaves as [Dispatchers.Default].
   *
   * The default [CoroutineDispatcher] that is used by all standard builders like
   * [launch][kotlinx.coroutines.launch], [async][kotlinx.coroutines.async], etc if no dispatcher
   * nor any other [ContinuationInterceptor] is specified in their context.
   *
   * It is backed by a shared pool of threads on JVM. By default, the maximal level of parallelism
   * used by this dispatcher is equal to the number of CPU cores, but is at least two. Level of
   * parallelism X guarantees that no more than X tasks can be executed in this dispatcher in
   * parallel.
   */
  public val Default: CoroutineDispatcher

  /**
   * The Main [CoroutineDispatcher] that behaves as [Dispatchers.Main].
   *
   * A coroutine dispatcher that is confined to the Main thread operating with UI objects. This
   * dispatcher can be used either directly or via [MainScope] factory. Usually such dispatcher is
   * single-threaded.
   *
   * Access to this property may throw [IllegalStateException] if no main thread dispatchers are
   * present in the classpath.
   *
   * Depending on platform and classpath it can be mapped to different dispatchers:
   * - On JS and Native it is equivalent of [Default] dispatcher.
   * - On JVM it is either Android main thread dispatcher, JavaFx or Swing EDT dispatcher. It is
   *   chosen by
   *   [`ServiceLoader`](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html).
   *
   * In order to work with `Main` dispatcher, the following artifacts should be added to project
   * runtime dependencies:
   * - `kotlinx-coroutines-android` for Android Main thread dispatcher
   * - `kotlinx-coroutines-javafx` for JavaFx Application thread dispatcher
   * - `kotlinx-coroutines-swing` for Swing EDT dispatcher
   *
   * In order to set a custom `Main` dispatcher for testing purposes, add the
   * `kotlinx-coroutines-test` artifact to project test dependencies.
   *
   * Implementation note: [MainCoroutineDispatcher.immediate] is not supported on Native and JS
   * platforms.
   */
  public val Main: MainCoroutineDispatcher

  /**
   * The IO [CoroutineDispatcher] that behaves as [Dispatchers.IO]
   *
   * A coroutine dispatcher that is not confined to any specific thread. It executes initial
   * continuation of the coroutine in the current call-frame and lets the coroutine resume in
   * whatever thread that is used by the corresponding suspending function, without mandating any
   * specific threading policy. Nested coroutines launched in this dispatcher form an event-loop to
   * avoid stack overflows.
   *
   * ### Event loop
   * Event loop semantics is a purely internal concept and have no guarantees on the order of
   * execution except that all queued coroutines will be executed on the current thread in the
   * lexical scope of the outermost unconfined coroutine.
   *
   * For example, the following code:
   * ```
   * withContext(Dispatchers.Unconfined) {
   *    println(1)
   *    withContext(Dispatchers.Unconfined) { // Nested unconfined
   *        println(2)
   *    }
   *    println(3)
   * }
   * println("Done")
   * ```
   *
   * Can print both "1 2 3" and "1 3 2", this is an implementation detail that can be changed. But
   * it is guaranteed that "Done" will be printed only when both `withContext` are completed.
   *
   * Note that if you need your coroutine to be confined to a particular thread or a thread-pool
   * after resumption, but still want to execute it in the current call-frame until its first
   * suspension, then you can use an optional [CoroutineStart] parameter in coroutine builders like
   * [launch][kotlinx.coroutines.launch] and [async][kotlinx.coroutines.async] setting it to the
   * value of [CoroutineStart.UNDISPATCHED].
   */
  public val IO: CoroutineDispatcher

  /**
   * The Unconfined [CoroutineDispatcher] that behaves as [Dispatchers.Unconfined]
   *
   * The [CoroutineDispatcher] that is designed for offloading blocking IO tasks to a shared pool of
   * threads.
   *
   * Additional threads in this pool are created and are shutdown on demand. The number of threads
   * used by tasks in this dispatcher is limited by the value of
   * "`kotlinx.coroutines.io.parallelism`" ([IO_PARALLELISM_PROPERTY_NAME]) system property. It
   * defaults to the limit of 64 threads or the number of cores (whichever is larger).
   *
   * Moreover, the maximum configurable number of threads is capped by the
   * `kotlinx.coroutines.scheduler.max.pool.size` system property. If you need a higher number of
   * parallel threads, you should use a custom dispatcher backed by your own thread pool.
   *
   * ### Implementation note
   *
   * This dispatcher shares threads with the [Default][Dispatchers.Default] dispatcher, so using
   * `withContext(Dispatchers.IO) { ... }` when already running on the
   * [Default][Dispatchers.Default] dispatcher does not lead to an actual switching to another
   * thread &mdash; typically execution continues in the same thread. As a result of thread sharing,
   * more than 64 (default parallelism) threads can be created (but not used) during operations over
   * IO dispatcher.
   */
  public val Unconfined: CoroutineDispatcher
}
