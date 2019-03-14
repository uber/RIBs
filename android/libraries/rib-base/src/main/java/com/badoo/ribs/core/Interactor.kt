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
package com.badoo.ribs.core

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.CallSuper
import com.badoo.ribs.android.IntentCreator
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.autodispose.LifecycleEndedException
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.rib.core.lifecycle.InteractorEvent
import com.uber.rib.core.lifecycle.InteractorEvent.ACTIVE
import com.uber.rib.core.lifecycle.InteractorEvent.INACTIVE
import com.badoo.ribs.core.view.RibView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import java.util.UUID

/**
 * The base implementation for all [Interactor]s.
 *
 * @param <C> the type of Configuration this Interactor can expect to push to its [Router].
 * @param <V> the type of [RibView].
 **/
abstract class Interactor<C : Parcelable, V : RibView>(
    protected val router: Router<C, V>,
    private val disposables: Disposable?
) : LifecycleScopeProvider<InteractorEvent>, LifecycleOwner, Identifiable {

    // TODO make private / or remove altogether if activity launching is refactored
    lateinit var node: Node<V>
        internal set
    private val ribLifecycleRegistry = LifecycleRegistry(this)
    private val viewLifecycleRegistry = LifecycleRegistry(this)

    // todo these are leftovers, reconsider them
    private val behaviorRelay = BehaviorRelay.create<InteractorEvent>()
    private val lifecycleRelay = behaviorRelay.toSerialized()

    val isAttached: Boolean
        get() = behaviorRelay.value == ACTIVE

    internal var tag = "${this::class.java.name}.${UUID.randomUUID()}"
        private set

    override val id: String =
        tag

    // todo these are leftovers, reconsider them
    override fun lifecycle(): Observable<InteractorEvent> =
        lifecycleRelay.hide()

    fun onAttach(savedInstanceState: Bundle?) {
        tag = savedInstanceState?.getString(KEY_TAG) ?: tag
        lifecycleRelay.accept(ACTIVE)
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        onAttach(ribLifecycleRegistry, savedInstanceState)
    }

    protected open fun onAttach(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
    }

    fun onDetach() {
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        disposables?.dispose()
        lifecycleRelay.accept(INACTIVE)
    }

    fun onViewCreated(view: V) {
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        onViewCreated(view, viewLifecycleRegistry)
    }

    protected open fun onViewCreated(view: V, viewLifecycle: Lifecycle) {
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
     * Handle an activity back press.
     *
     * @return TRUE if the interactor handled the back press and no further action is necessary.
     */
    open fun handleBackPress(): Boolean =
        false

    fun startActivityForResult(requestCode: Int, intentCreator: IntentCreator.() -> Intent) {
        node.startActivityForResult(requestCode, intentCreator)
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        false

    @CallSuper
    open fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_TAG, tag)
    }

    override fun getLifecycle(): Lifecycle =
        ribLifecycleRegistry

    // todo these are leftovers, reconsider them
    override fun correspondingEvents(): Function<InteractorEvent, InteractorEvent> =
        LIFECYCLE_MAP_FUNCTION

    // todo these are leftovers, reconsider them
    override fun peekLifecycle(): InteractorEvent? =
        behaviorRelay.value

    companion object {
        internal const val KEY_TAG = "interactor.tag"

        // todo these are leftovers, reconsider them
        private val LIFECYCLE_MAP_FUNCTION: Function<InteractorEvent, InteractorEvent> =
            Function { interactorEvent ->
                when (interactorEvent) {
                    ACTIVE -> INACTIVE
                    else -> throw LifecycleEndedException()
                }
            }
    }
}
