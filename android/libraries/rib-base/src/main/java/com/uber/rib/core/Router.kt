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
import android.os.Parcelable
import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import android.support.annotation.VisibleForTesting
import android.util.SparseArray
import android.view.ViewGroup
import com.uber.rib.core.requestcode.RequestCodeRegistry
import com.uber.rib.core.routing.action.RoutingAction
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Responsible for handling the addition and removal of child routers.
 **/
open class Router<V : RibView>(
    private val viewFactory: ViewFactory<V>?,
    val interactor: Interactor<V, *>,
    private val ribRefWatcher: RibRefWatcher = RibRefWatcher.getInstance()
) {
    companion object {
        @VisibleForTesting internal val KEY_CHILD_ROUTERS = "router.children"
        @VisibleForTesting internal val KEY_INTERACTOR = "router.interactor"
        private const val KEY_RIB_ID = "rib.id"
        private const val KEY_VIEW_STATE = "view.state"
        private val requestCodeRegistry = RequestCodeRegistry(8)
    }

    private var savedInstanceState: Bundle? = null
    val children = CopyOnWriteArrayList<Router<*>>()
    private var tag: String = "${this::class.java.name}.${UUID.randomUUID()}"
    private var ribId: Int? = null
    internal var view: V? = null
    protected var parentViewGroup: ViewGroup? = null

    private lateinit var attachPermanentParts: RoutingAction<V>
    protected open val permanentParts: List<() -> Router<*>> = emptyList()
    private var savedViewState: SparseArray<Parcelable> = SparseArray()

    init {
        interactor.router = this
    }

    private fun attachPermanentParts() {
        permanentParts.forEach {
            addChild(it()) // fixme save and restore these as well
        }
    }

    private fun generateRibId(): Int =
        requestCodeRegistry.generateGroupId(tag)

    private fun generateRequestCode(code: Int): Int =
        requestCodeRegistry.generateRequestCode(tag, code)

    private fun updateRibId(value: Int) {
        ribId = value
    }

    // todo move this to base router class so no casting is needed inside foreach
    fun onAttachToView(parentViewGroup: ViewGroup) {
        this.parentViewGroup = parentViewGroup
        view = createView(parentViewGroup)
        view?.let {
            it.androidView.restoreHierarchyState(savedViewState)
            parentViewGroup.addView(it.androidView)
            interactor.onViewCreated()
        }

        children.forEach {
            attachChildToView(it)
        }
    }

    private fun createView(parentViewGroup: ViewGroup): V? =
        viewFactory?.invoke(parentViewGroup)

    fun addChild(child: Router<*>, bundle: Bundle? = null) {
        attachChildToView(child)
        // todo refactor so that this branching is not necessary
        if (bundle != null) {
            attachChild(child, bundle)
        } else {
            attachChild(child)
        }
    }

    protected fun attachChildToView(child: Router<*>) {
        parentViewGroup?.let {
            child.onAttachToView(
                attachTargetForConfiguration(view, child, it)
            )
        }
    }

    open fun attachTargetForConfiguration(view: V?, child: Router<*>, parentViewGroup: ViewGroup): ViewGroup =
        view?.androidView ?: parentViewGroup

    fun removeChild(child: Router<*>) {
        detachChild(child)
        detachChildFromView(child, saveHierarchyState = false)
    }

    protected fun detachChildFromViewAndSaveHierarchyState(child: Router<*>) {
        detachChildFromView(child, saveHierarchyState = true)
    }

    protected fun detachChildFromView(child: Router<*>, saveHierarchyState: Boolean) {
        parentViewGroup?.let {
            child.onDetachFromView(
                parentViewGroup = attachTargetForConfiguration(view, child, it),
                saveHierarchyState = saveHierarchyState
            )
        }
    }

    fun onDetachFromView(parentViewGroup: ViewGroup, saveHierarchyState: Boolean) {
        children.forEach {
            detachChildFromView(
                child = it,
                saveHierarchyState = saveHierarchyState
            )
        }

        view?.let {
            if (saveHierarchyState) {
                it.androidView.saveHierarchyState(savedViewState)
            }

            parentViewGroup.removeView(it.androidView)
            interactor.onViewDestroyed()
        }

        view = null
        this.parentViewGroup = null
    }

    /**
     * Dispatch back press to the associated interactor.
     *
     * @return TRUE if the interactor handled the back press and no further action is necessary.
     */
    @CallSuper
    open fun handleBackPress(): Boolean {
        ribRefWatcher.logBreadcrumb("BACKPRESS", null, null)
        return interactor.handleBackPress()
    }

    /**
     * Called when a router is being attached. Router subclasses can perform setup here for anything
     * that is needed again but is cleaned up in willDetach(). Use didLoad() if the setup is only
     * needed once.
     */
    protected fun willAttach() {}

    /**
     * Called when a router is being a detached, router subclasses should perform any required clean
     * up here.
     */
    protected fun willDetach() {}

    /**
     * Attaches a child router to this router.
     *
     * @param childRouter the [Router] to be attached.
     * @param tag an identifier to namespace saved instance state [Bundle] objects.
     */
    @MainThread
    protected fun attachChild(childRouter: Router<*>) {
        children.add(childRouter)
        ribRefWatcher.logBreadcrumb(
            "ATTACHED", childRouter.javaClass.simpleName, this.javaClass.simpleName
        )
        var childBundle: Bundle? = null
        if (this.savedInstanceState != null) {
            val previousChildren = this.savedInstanceState!!.getBundle(KEY_CHILD_ROUTERS)
            if (previousChildren != null) {
                childBundle = previousChildren.getBundle(tag)
            }
        }

        childRouter.dispatchAttach(childBundle)
    }

    /**
     * Attaches a child router to this router.
     *
     * @param childRouter the [Router] to be attached.
     */
    @MainThread
    protected fun attachChild(childRouter: Router<*>, bundle: Bundle) {
        children.add(childRouter)
        ribRefWatcher.logBreadcrumb(
            "ATTACHED", childRouter.javaClass.simpleName, this.javaClass.simpleName
        )

        childRouter.dispatchAttach(bundle)
    }

    /**
     * Detaches the {@param childFactory} from the current [Interactor]. NOTE: No consumers of
     * this API should ever keep a reference to the detached child router, leak canary will enforce
     * that it gets garbage collected.
     *
     *
     * If you need to keep references to previous routers, use [RouterNavigator].
     *
     * @param childRouter the [Router] to be detached.
     */
    @MainThread
    protected fun detachChild(childRouter: Router<*>) {
        children.remove(childRouter)

        val interactor = childRouter.interactor
        ribRefWatcher.watchDeletedObject(interactor)
        ribRefWatcher.logBreadcrumb(
            "DETACHED", childRouter.javaClass.simpleName, this.javaClass.simpleName
        )
        if (savedInstanceState != null) {
            var childrenBundles: Bundle? = savedInstanceState!!.getBundle(KEY_CHILD_ROUTERS)
            if (childrenBundles == null) {
                childrenBundles = Bundle()
                savedInstanceState!!.putBundle(KEY_CHILD_ROUTERS, childrenBundles)
            }
            childrenBundles.putBundle(childRouter.tag, null)
        }

        childRouter.dispatchDetach()
    }

    @CallSuper
    open fun dispatchAttach(savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState

        updateRibId(savedInstanceState?.getInt(KEY_RIB_ID, generateRibId()) ?: generateRibId())
        savedViewState = savedInstanceState?.getSparseParcelableArray<Parcelable>(KEY_VIEW_STATE) ?: SparseArray()

        willAttach()
        attachPermanentParts()

        var interactorBundle: Bundle? = null
        if (this.savedInstanceState != null) {
            interactorBundle = this.savedInstanceState!!.getBundle(KEY_INTERACTOR)
        }
        interactor.dispatchAttach(interactorBundle)
    }

    open fun dispatchDetach() {
        interactor.dispatchDetach()
        willDetach()

        for (child in children) {
            detachChild(child)
        }
    }

    open fun saveInstanceState(outState: Bundle) {
        outState.putInt(KEY_RIB_ID, ribId ?: generateRibId().also { updateRibId(it) })
        outState.putSparseParcelableArray(KEY_VIEW_STATE, savedViewState)
        saveInteractorState(outState)
        saveStateOfChildren(outState)
    }

    private fun saveInteractorState(outState: Bundle) {
        Bundle().let {
            interactor.onSaveInstanceState(it)
            outState.putBundle(KEY_INTERACTOR, it)
        }
    }

    private fun saveStateOfChildren(outState: Bundle) {
        val childBundles = Bundle()
        for (child in children) {
            Bundle().let {
                child.saveInstanceState(it)
                childBundles.putBundle(child.tag, it)
            }

        }
        outState.putBundle(KEY_CHILD_ROUTERS, childBundles)
    }

    fun onStart() {
        interactor.onStart()
        children.forEach { it.onStart() }
    }

    fun onStop() {
        interactor.onStop()
        children.forEach { it.onStop() }
    }

    fun onResume() {
        interactor.onResume()
        children.forEach { it.onResume() }
    }

    fun onPause() {
        interactor.onPause()
        children.forEach { it.onPause() }
    }
}

