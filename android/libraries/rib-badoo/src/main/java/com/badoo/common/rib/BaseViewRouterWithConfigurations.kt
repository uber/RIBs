package com.badoo.common.rib

import android.os.Bundle
import android.os.Parcelable
import com.badoo.common.rib.routing.RibConnector
import com.badoo.common.rib.routing.action.RoutingAction
import com.badoo.common.rib.routing.backstack.BackStackManager
import com.badoo.common.rib.routing.backstack.BackStackManager.Wish.NewRoot
import com.badoo.common.rib.routing.backstack.BackStackManager.Wish.Pop
import com.badoo.common.rib.routing.backstack.BackStackManager.Wish.Push
import com.badoo.common.rib.routing.backstack.BackStackManager.Wish.Replace
import com.badoo.common.rib.routing.backstack.BackStackManager.Wish.ShrinkToBundles
import com.badoo.common.rib.routing.backstack.BackStackManager.Wish.TearDown
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.binder.Binder
import com.uber.rib.core.RibAndroidView

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class BaseViewRouterWithConfigurations<C : Parcelable, V : RibAndroidView, I : BaseInteractor<V, *>>(
    private val viewFactory: ViewFactory<V>?,
    interactor: I,
    private val initialConfiguration: C
) : BaseViewRouter<V>(
    viewFactory,
    interactor
) {

    private val binder = Binder()
    private lateinit var timeCapsule: AndroidTimeCapsule // = AndroidTimeCapsule(null)
    private lateinit var backStackManager: BackStackManager<C>
    protected val configuration: C?
        get() = backStackManager.state.current
    private var currentRoutingAction: RoutingAction<V>? = null

    override fun dispatchAttach(savedInstanceState: Bundle?, tag: String) {
        super.dispatchAttach(savedInstanceState, tag)
        timeCapsule = AndroidTimeCapsule(savedInstanceState)
        initConfigurationManager()
    }

    private fun initConfigurationManager() {
        backStackManager = BackStackManager(
            this::resolveConfiguration,
            RibConnector.from(
                this::addChild,
                this::attachChildToView,
                this::detachChildFromViewAndSaveHierarchyState,
                this::removeChild
            ),
            initialConfiguration,
            timeCapsule,
            javaClass.name
        )
    }

    abstract fun resolveConfiguration(configuration: C): RoutingAction<V>

    override fun saveInstanceState(outState: Bundle) {
        super.saveInstanceState(outState)
        backStackManager.accept(ShrinkToBundles())
        timeCapsule.saveState(outState)
    }

    override fun dispatchDetach() {
        super.willDetach()
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

    fun popBackStack(): Boolean {
        return if (backStackManager.state.canPop) {
            backStackManager.accept(Pop())
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
