package com.badoo.common.rib

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.annotation.CallSuper
import com.badoo.mvicore.android.lifecycle.CreateDestroyBinderLifecycle
import com.badoo.mvicore.android.lifecycle.ResumePauseBinderLifecycle
import com.badoo.mvicore.android.lifecycle.StartStopBinderLifecycle
import com.badoo.mvicore.binder.Binder
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibAndroidView
import io.reactivex.disposables.Disposable

abstract class BaseInteractor<V : RibAndroidView, R : BaseViewRouter<V>>(
    private val disposables: List<Disposable>
) : Interactor<V>(), LifecycleOwner {

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
        // fixme
        onViewCreated((router as BaseViewRouter<V>).view!!, viewLifecycleRegistry)
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
        disposables.forEach { it.dispose() }
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
