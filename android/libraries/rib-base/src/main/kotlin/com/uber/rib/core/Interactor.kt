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
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.uber.autodispose.lifecycle.LifecycleScopes
import com.uber.rib.core.lifecycle.InteractorEvent
import io.reactivex.CompletableSource
import io.reactivex.Observable
import javax.inject.Inject

/**
 * The base implementation for all [Interactor]s.
 *
 * @param <P> the type of [Presenter].
 * @param <R> the type of [Router].
 */
abstract class Interactor<P : Any, R : Router<*>> : LifecycleScopeProvider<InteractorEvent> {
  @Inject
  lateinit var injectedPresenter: P
  internal var actualPresenter: P? = null
  private val behaviorRelay = BehaviorRelay.create<InteractorEvent>()
  private val lifecycleRelay = behaviorRelay.toSerialized()

  /** @return the router for this interactor. */
  internal var router: R? = null

  constructor()

  protected constructor(presenter: P) {
    this.actualPresenter = presenter
  }

  /** @return an observable of this controller's lifecycle events.
   */
  override fun lifecycle(): Observable<InteractorEvent> {
    return lifecycleRelay.hide()
  }

  /** @return true if the controller is attached, false if not.
   */
  open val isAttached: Boolean
    get() = behaviorRelay.value === InteractorEvent.ACTIVE

  /**
   * Called when attached. The presenter will automatically be added when this happens.
   *
   * @param savedInstanceState the saved [Bundle].
   */
  @CallSuper
  protected open fun didBecomeActive(savedInstanceState: Bundle?) {
  }

  /**
   * Handle an activity back press.
   *
   * @return whether this interactor took action in response to a back press.
   */
  open fun handleBackPress(): Boolean {
    return false
  }

  /**
   * Called when detached. The [Interactor] should do its cleanup here. Note: View will be
   * removed automatically so [Interactor] doesn't have to remove its view here.
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
  open fun onSaveInstanceState(outState: Bundle) {}

  internal open fun dispatchAttach(savedInstanceState: Bundle?) {
    lifecycleRelay.accept(InteractorEvent.ACTIVE)
    (getPresenter() as? Presenter)?.dispatchLoad()
    didBecomeActive(savedInstanceState)
  }

  internal open fun dispatchDetach(): P {
    (getPresenter() as? Presenter)?.dispatchUnload()
    willResignActive()
    lifecycleRelay.accept(InteractorEvent.INACTIVE)
    return getPresenter()
  }

  open fun getRouter(): R {
    checkNotNull(router) { "Attempting to get interactor's router before being set." }
    return router!!
  }

  protected open fun setRouter(router: Router<*>) {
    if (this.router != null) {
      throw IllegalStateException("Attempting to set interactor's router after it has been set.")
    }
    this.router = router as R
  }

  internal fun setRouterInternal(router: Router<*>) {
    setRouter(router)
  }

  /** @return the currently attached presenter if there is one
   */
  @VisibleForTesting
  private fun getPresenter(): P {
    val presenter: P? = try {
      if (actualPresenter != null)
        actualPresenter
      else
        injectedPresenter
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

  override fun correspondingEvents(): CorrespondingEventsFunction<InteractorEvent> {
    return LIFECYCLE_MAP_FUNCTION
  }

  override fun peekLifecycle(): InteractorEvent {
    return behaviorRelay.value!!
  }

  override fun requestScope(): CompletableSource {
    return LifecycleScopes.resolveScopeFromLifecycle(this)
  }

  private object NoOpPresenter : Presenter()

  companion object {
    private val LIFECYCLE_MAP_FUNCTION = CorrespondingEventsFunction { interactorEvent: InteractorEvent ->
      when (interactorEvent) {
        InteractorEvent.ACTIVE -> return@CorrespondingEventsFunction InteractorEvent.INACTIVE
        else -> throw LifecycleEndedException()
      }
    }
  }
}
