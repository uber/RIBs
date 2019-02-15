package com.badoo.common.rib

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.support.annotation.CallSuper
import com.badoo.mvicore.binder.Binder
import com.badoo.mvicore.android.lifecycle.CreateDestroyBinderLifecycle
import com.badoo.mvicore.android.lifecycle.StartStopBinderLifecycle
import com.badoo.mvicore.android.lifecycle.ResumePauseBinderLifecycle
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibView

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class BaseInteractor<V : RibView, R : BaseViewRouter<V, *>> : Interactor<R>(), LifecycleOwner {

    private val ribLifecycleRegistry = LifecycleRegistry(this)
    private val viewLifecycleRegistry = LifecycleRegistry(this)

    final override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        didBecomeActive(ribLifecycleRegistry, savedInstanceState)
    }

    open fun didBecomeActive(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
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

    override fun willResignActive() {
        super.willResignActive()
        viewLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) // todo probably this is not needed?
        ribLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun getLifecycle(): Lifecycle =
        ribLifecycleRegistry

    fun Lifecycle.createDestroy(f: Binder.() -> Unit) {
        Binder(CreateDestroyBinderLifecycle(this)).apply(f)
    }

    fun Lifecycle.startStop(f: Binder.() -> Unit) {
        Binder(StartStopBinderLifecycle(this)).apply(f)
    }

    fun Lifecycle.resumePause(f: Binder.() -> Unit) {
        Binder(ResumePauseBinderLifecycle(this)).apply(f)
    }
}
