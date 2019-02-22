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
package com.uber.rib.core

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.annotation.CallSuper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.autodispose.LifecycleEndedException
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.InteractorEvent.ACTIVE
import com.uber.rib.core.lifecycle.InteractorEvent.INACTIVE
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

/**
 * The base implementation for all [Interactor]s.
 *
 * @param <V> the type of [RibView].
 * @param <R> the type of [Router].
 **/
abstract class Interactor<V : RibView, R : Router<V>>(
    private val disposables: List<Disposable>
) : LifecycleScopeProvider<InteractorEvent>, LifecycleOwner {

    private val ribLifecycleRegistry = LifecycleRegistry(this)
    private val viewLifecycleRegistry = LifecycleRegistry(this)

    // todo these are leftovers, reconsider them
    private val behaviorRelay = BehaviorRelay.create<InteractorEvent>()
    private val lifecycleRelay = behaviorRelay.toSerialized()

    internal lateinit var router: Router<V>

    val isAttached: Boolean
        get() = behaviorRelay.value == ACTIVE

    // todo these are leftovers, reconsider them
    override fun lifecycle(): Observable<InteractorEvent> =
        lifecycleRelay.hide()

    /**
     * Called when attached. The presenter will automatically be added when this happens.
     *
     * @param savedInstanceState the saved [Bundle].
     */
    @CallSuper
    protected fun didBecomeActive(savedInstanceState: Bundle?) {
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        didBecomeActive(ribLifecycleRegistry, savedInstanceState)
    }

    protected open fun didBecomeActive(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
    }

    @CallSuper
    fun onViewCreated() {
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        onViewCreated(router.view!!, viewLifecycleRegistry)
    }

    open fun onViewCreated(view: V, viewLifecycle: Lifecycle) {
    }

    fun onViewDestroyed() {
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    fun onStart() {
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    // todo call this when removed from view
    fun onStop() {
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun onResume() {
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    // todo call this when removed from view
    fun onPause() {
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    /**
     * Called when detached. The [Interactor] should do its cleanup here.
     */
    fun willResignActive() {
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) // todo probably this is not needed?
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        disposables.forEach { it.dispose() }
    }

    override fun getLifecycle(): Lifecycle =
        ribLifecycleRegistry

    /**
     * Handle an activity back press.
     *
     * @return TRUE if the interactor handled the back press and no further action is necessary.
     */
    open fun handleBackPress(): Boolean {
        return false
    }

    /**
     * Called when saving state.
     *
     * @param outState the saved [Bundle].
     */
    open fun onSaveInstanceState(outState: Bundle) {}

    fun dispatchAttach(savedInstanceState: Bundle?) {
        lifecycleRelay.accept(ACTIVE)

        didBecomeActive(savedInstanceState)
    }

    fun dispatchDetach() {
        willResignActive()

        lifecycleRelay.accept(INACTIVE)
    }

    // todo these are leftovers, reconsider them
    override fun correspondingEvents(): Function<InteractorEvent, InteractorEvent> =
        LIFECYCLE_MAP_FUNCTION

    // todo these are leftovers, reconsider them
    override fun peekLifecycle(): InteractorEvent? =
        behaviorRelay.value

    // todo these are leftovers, reconsider them
    companion object {
        private val LIFECYCLE_MAP_FUNCTION: Function<InteractorEvent, InteractorEvent> =
            Function { interactorEvent ->
                when (interactorEvent) {
                    ACTIVE -> INACTIVE
                    else -> throw LifecycleEndedException()
                }
            }
    }
}
