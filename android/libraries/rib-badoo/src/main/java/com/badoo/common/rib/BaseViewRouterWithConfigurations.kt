package com.badoo.common.rib

import android.os.Parcelable
import com.badoo.common.rib.routing.action.RoutingAction
import com.badoo.common.rib.routing.backstack.RouterBackStackManager
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.NewRoot
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Pop
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Push
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Replace
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.ShrinkToBundles
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.binder.Binder
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibView

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class BaseViewRouterWithConfigurations<C : Parcelable, V : RibView, I : Interactor<*>>(
    private val viewFactory: ViewFactory<V>?,
    interactor: I,
    private val initialConfiguration: C
) : BaseViewRouter<V, I>(
    viewFactory,
    interactor
) {
    private val binder = Binder()
    private lateinit var timeCapsule: AndroidTimeCapsule // = AndroidTimeCapsule(null)
    private lateinit var configurationManager: RouterBackStackManager<C>
    protected val configuration: C?
        get() = configurationManager.state.current
    private var currentRoutingAction: RoutingAction<V>? = null

    override fun dispatchAttach(savedInstanceState: Bundle?, tag: String) {
        super.dispatchAttach(savedInstanceState, tag)
        timeCapsule = AndroidTimeCapsule(savedInstanceState?.wrappedBundle)
        initConfigurationManager()
    }

    private fun initConfigurationManager() {
        configurationManager = RouterBackStackManager(
            this::resolveConfiguration,
            this::addChild,
            this::attachChildToView,
            this::detachChildFromViewAndSaveHierarchyState,
            this::removeChild,
            initialConfiguration,
            timeCapsule,
            javaClass.name
        )
    }

    abstract fun resolveConfiguration(configuration: C): RoutingAction<V>

    override fun saveInstanceState(outState: Bundle) {
        super.saveInstanceState(outState)
        configurationManager.accept(ShrinkToBundles())
        timeCapsule.saveState(outState.wrappedBundle)
    }

    override fun dispatchDetach() {
        super.willDetach()
        // todo consider if non-rib backstack elements should receive onLeave() callback here?
        binder.clear()
    }

    fun replace(configuration: C) {
        configurationManager.accept(Replace(configuration))
    }

    fun push(configuration: C) {
        configurationManager.accept(Push(configuration))
    }

    fun newRoot(configuration: C) {
        configurationManager.accept(NewRoot(configuration))
    }

    fun popBackStack(): Boolean {
        return if (configurationManager.state.canPop) {
            configurationManager.accept(Pop())
            true
        } else {
            false
        }
    }

    override fun handleBackPress(): Boolean =
        when {
            children.any { it.handleBackPress() } -> true
            popBackStack() -> true
            else -> super.handleBackPress()
        }

}
