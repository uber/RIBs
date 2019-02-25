package com.uber.rib.core

import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.binder.Binder
import com.uber.rib.core.routing.RibConnector
import com.uber.rib.core.routing.action.RoutingAction
import com.uber.rib.core.routing.backstack.BackStackManager
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.NewRoot
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.Pop
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.Push
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.Replace
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.ShrinkToBundles
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.TearDown

abstract class Router<C : Parcelable, V : RibView>(
    private val initialConfiguration: C
) {
    private val binder = Binder()
    private lateinit var timeCapsule: AndroidTimeCapsule
    private lateinit var backStackManager: BackStackManager<C>
    protected val configuration: C?
        get() = backStackManager.state.current.configuration

    lateinit var node: Node<V>

    fun dispatchAttach(savedInstanceState: Bundle?) {
        timeCapsule = AndroidTimeCapsule(savedInstanceState)
        attachPermanentParts()
        initConfigurationManager()
    }

    private fun attachPermanentParts() {
        permanentParts.forEach {
            node.attachChild(it()) // fixme save and restore these as well
        }
    }

    protected open val permanentParts: List<() -> Node<*>> = emptyList()

    private fun initConfigurationManager() {
        backStackManager = BackStackManager(
            this::resolveConfiguration,
            RibConnector.from(
                node::attachChild,
                node::attachChildView,
                node::detachChildView,
                node::detachChild
            ),
            initialConfiguration,
            timeCapsule
        )
    }

    abstract fun resolveConfiguration(configuration: C): RoutingAction<V>

    open fun getParentViewForChild(child: Node<*>, view: V?, parent: ViewGroup): ViewGroup =
        view?.androidView ?: parent

    fun saveInstanceState(outState: Bundle) {
        backStackManager.accept(ShrinkToBundles())
        timeCapsule.saveState(outState)
    }

    fun dispatchDetach() {
        backStackManager.accept(TearDown())
        binder.clear()
    }

    fun replace(configuration: C) {
        backStackManager.accept(Replace(configuration))
    }

    fun push(configuration: C) {
        backStackManager.accept(Push(configuration))
    }

    fun newRoot(configuration: C) {
        backStackManager.accept(NewRoot(configuration))
    }

    fun popBackStack(): Boolean =
        if (backStackManager.state.canPop) {
            backStackManager.accept(Pop())
            true
        } else {
            false
        }
}
