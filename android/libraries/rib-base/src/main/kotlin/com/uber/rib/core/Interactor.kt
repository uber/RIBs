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
import androidx.annotation.VisibleForTesting
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.rib.core.lifecycle.InteractorEvent
import io.reactivex.CompletableSource
import io.reactivex.Observable
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.rx2.asObservable

/**
 * The base implementation for all [Interactor]s.
 *
 * @param <P> the type of [Presenter].
 * @param <R> the type of [Router].
 */
public abstract class Interactor<P : Any, R : Router<*>>() : InteractorType {
  @Inject public lateinit var injectedPresenter: P
  internal var actualPresenter: P? = null
  private val _lifecycleFlow = MutableSharedFlow<InteractorEvent>(1, 0, BufferOverflow.DROP_OLDEST)
  public open val lifecycleFlow: SharedFlow<InteractorEvent>
    get() = _lifecycleFlow

  @Volatile private var _lifecycleObservable: Observable<InteractorEvent>? = null
  private val lifecycleObservable
    get() = ::_lifecycleObservable.setIfNullAndGet { lifecycleFlow.asObservable() }

  private val routerDelegate = InitOnceProperty<R>()

  /** @return the router for this interactor. */
  public open var router: R by routerDelegate
    protected set

  protected constructor(presenter: P) : this() {
    this.actualPresenter = presenter
  }

  // ---- LifecycleScopeProvider overrides ---- //

  final override fun lifecycle(): Observable<InteractorEvent> = lifecycleObservable

  final override fun correspondingEvents(): CorrespondingEventsFunction<InteractorEvent> =
    LIFECYCLE_MAP_FUNCTION

  final override fun peekLifecycle(): InteractorEvent? = lifecycleFlow.replayCache.lastOrNull()

  final override fun requestScope(): CompletableSource =
    lifecycleFlow.asScopeCompletable(lifecycleRange)

  // ---- InteractorType overrides ---- //

  override fun isAttached(): Boolean =
    _lifecycleFlow.replayCache.lastOrNull() == InteractorEvent.ACTIVE

  override fun handleBackPress(): Boolean = false

  /**
   * Called when attached. The presenter will automatically be added when this happens.
   *
   * @param savedInstanceState the saved [Bundle].
   */
  @CallSuper protected open fun didBecomeActive(savedInstanceState: Bundle?) {}

  /**
   * Called when detached. The [Interactor] should do its cleanup here. Note: View will be removed
   * automatically so [Interactor] doesn't have to remove its view here.
   */
  protected open fun willResignActive() {}

  internal fun onSaveInstanceStateInternal(outState: Bundle) {
    onSaveInstanceState(outState)
  }

  /**
   * Called when saving state.
   *
   * @param outState the saved [Bundle].
   */
  protected open fun onSaveInstanceState(outState: Bundle) {}

  public open fun dispatchAttach(savedInstanceState: Bundle?) {
    _lifecycleFlow.tryEmit(InteractorEvent.ACTIVE)
    (getPresenter() as? Presenter)?.dispatchLoad()
    didBecomeActive(savedInstanceState)
  }

  public open fun dispatchDetach(): P {
    (getPresenter() as? Presenter)?.dispatchUnload()
    willResignActive()
    _lifecycleFlow.tryEmit(InteractorEvent.INACTIVE)
    return getPresenter()
  }

  internal fun setRouterInternal(router: Router<*>) {
    if (routerDelegate != null) {
      this.router = router as R
    }
  }

  /** @return the currently attached presenter if there is one */
  @VisibleForTesting
  private fun getPresenter(): P {
    val presenter: P? =
      try {
        if (actualPresenter != null) {
          actualPresenter
        } else {
          injectedPresenter
        }
      } catch (e: UninitializedPropertyAccessException) {
        actualPresenter
      }
    checkNotNull(presenter) { "Attempting to get interactor's presenter before being set." }
    return presenter
  }

  @VisibleForTesting
  internal fun setPresenter(presenter: P) {
    actualPresenter = presenter
  }

  private inner class InitOnceProperty<T> : ReadWriteProperty<Any, T> {
    private var backingField: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
      if (backingField == null) {
        throw IllegalStateException("Attempting to get value before it has been set.")
      }
      return backingField as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
      if (backingField != null) {
        throw IllegalStateException("Attempting to set value after it has been set.")
      } else {
        backingField = value
      }
    }
  }

  public companion object {
    @get:JvmSynthetic internal val lifecycleRange = InteractorEvent.ACTIVE..InteractorEvent.INACTIVE

    private val LIFECYCLE_MAP_FUNCTION =
      CorrespondingEventsFunction { interactorEvent: InteractorEvent ->
        when (interactorEvent) {
          InteractorEvent.ACTIVE -> return@CorrespondingEventsFunction InteractorEvent.INACTIVE
          else -> throw LifecycleEndedException()
        }
      }
  }
}
