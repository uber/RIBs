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

import androidx.annotation.CallSuper
import com.uber.autodispose.ScopeProvider
import com.uber.rib.core.lifecycle.PresenterEvent
import io.reactivex.CompletableSource
import io.reactivex.Observable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.rx2.asObservable
import org.checkerframework.checker.guieffect.qual.UIEffect

/**
 * Contains presentation logic. This class exists mainly for legacy reasons. In the past we believed
 * it was useful to have a class between interactors and views to facilitate model transformations
 * and believed these transformations would be complex enough to require its own lifecycle. In
 * practice this caused confusion: if both a presenter and interactor can perform complex rx logic
 * it becomes unclear where you should write your bussiness logic.
 */
public abstract class Presenter : ScopeProvider {
  private val _lifecycleFlow = MutableSharedFlow<PresenterEvent>(1, 0, BufferOverflow.DROP_OLDEST)
  public open val lifecycleFlow: SharedFlow<PresenterEvent>
    get() = _lifecycleFlow

  @Volatile private var _lifecycleObservable: Observable<PresenterEvent>? = null
  private val lifecycleObservable
    get() = ::_lifecycleObservable.setIfNullAndGet { lifecycleFlow.asObservable() }

  /** @return `true` if the presenter is loaded, `false` if not. */
  protected var isLoaded: Boolean = false
    private set

  public open fun dispatchLoad() {
    isLoaded = true
    _lifecycleFlow.tryEmit(PresenterEvent.LOADED)
    didLoad()
  }

  public open fun dispatchUnload() {
    isLoaded = false
    willUnload()
    _lifecycleFlow.tryEmit(PresenterEvent.UNLOADED)
  }

  /** Tells the presenter that it has finished loading. */
  @CallSuper protected open fun didLoad() {}

  /**
   * Tells the presenter that it will be destroyed. Presenter subclasses should perform any required
   * cleanup here.
   */
  @UIEffect @CallSuper protected open fun willUnload() {}

  /** @return an observable of this controller's lifecycle events. */
  public fun lifecycle(): Observable<PresenterEvent> = lifecycleObservable

  final override fun requestScope(): CompletableSource =
    lifecycleFlow.asScopeCompletable(lifecycleRange)

  internal companion object {
    @get:JvmSynthetic internal val lifecycleRange = PresenterEvent.LOADED..PresenterEvent.UNLOADED
  }
}
