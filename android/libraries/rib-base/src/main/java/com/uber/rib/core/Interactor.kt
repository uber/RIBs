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

import android.os.Bundle
import android.support.annotation.CallSuper

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import com.uber.autodispose.LifecycleEndedException
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.rib.core.lifecycle.InteractorEvent

import io.reactivex.Observable
import io.reactivex.functions.Function

import com.uber.rib.core.lifecycle.InteractorEvent.ACTIVE
import com.uber.rib.core.lifecycle.InteractorEvent.INACTIVE

/**
 * The base implementation for all [Interactor]s.
 *
 * @param <R> the type of [Router].
</R> */
abstract class Interactor : LifecycleScopeProvider<InteractorEvent> {

    private val behaviorRelay = BehaviorRelay.create<InteractorEvent>()
    private val lifecycleRelay = behaviorRelay.toSerialized()

    open var router: Router? = null
        get() {
            if (field == null) {
                throw IllegalStateException("Attempting to get interactor's router before being set.")
            }

            return field
        }
        internal set(value) {
            if (field != null) {
                throw IllegalStateException(
                    "Attempting to set interactor's router after it has been set."
                )
            }

            field = value
        }

    /** @return true if the controller is attached, false if not.
     */
    val isAttached: Boolean
        get() = behaviorRelay.value == ACTIVE

    /** @return an observable of this controller's lifecycle events.
     */
    override fun lifecycle(): Observable<InteractorEvent> {
        return lifecycleRelay.hide()
    }

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
    fun handleBackPress(): Boolean {
        return false
    }

    /**
     * Called when detached. The [Interactor] should do its cleanup here. Note: View will be
     * removed automatically so [Interactor] doesn't have to remove its view here.
     */
    protected open fun willResignActive() {}

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

    override fun correspondingEvents(): Function<InteractorEvent, InteractorEvent> =
        LIFECYCLE_MAP_FUNCTION

    override fun peekLifecycle(): InteractorEvent? =
        behaviorRelay.value

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
