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
 * Responsible for handling the addition and removal of child nodes.
 **/
open class Node<V : RibView>(
    private val viewFactory: ViewFactory<V>?,
    val interactor: Interactor<V, *>,
    private val ribRefWatcher: RibRefWatcher = RibRefWatcher.getInstance()
) {
    companion object {
        @VisibleForTesting internal val KEY_CHILD_NODES = "node.children"
        @VisibleForTesting internal val KEY_INTERACTOR = "node.interactor"
        private const val KEY_RIB_ID = "rib.id"
        private const val KEY_VIEW_STATE = "view.state"
        private val requestCodeRegistry = RequestCodeRegistry(8)
    }

    private var savedInstanceState: Bundle? = null
    val children = CopyOnWriteArrayList<Node<*>>()
    protected var tag: String = "${this::class.java.name}.${UUID.randomUUID()}"
        private set
    private var ribId: Int? = null
    internal var view: V? = null
        private set
    protected var parentViewGroup: ViewGroup? = null

    private lateinit var attachPermanentParts: RoutingAction<V>
    protected open val permanentParts: List<() -> Node<*>> = emptyList()
    private var savedViewState: SparseArray<Parcelable> = SparseArray()

    init {
        interactor.node = this
    }

    private fun attachPermanentParts() {
        permanentParts.forEach {
            attachChild(it()) // fixme save and restore these as well
        }
    }

    private fun generateRibId(): Int =
        requestCodeRegistry.generateGroupId(tag)

    private fun generateRequestCode(code: Int): Int =
        requestCodeRegistry.generateRequestCode(tag, code)

    private fun updateRibId(value: Int) {
        ribId = value
    }

    fun attachToView(parentViewGroup: ViewGroup) {
        this.parentViewGroup = parentViewGroup
        view = createView(parentViewGroup)
        view?.let {
            it.androidView.restoreHierarchyState(savedViewState)
            parentViewGroup.addView(it.androidView)
            interactor.onViewCreated()
        }

        children.forEach {
            attachChildView(it)
        }
    }

    private fun createView(parentViewGroup: ViewGroup): V? =
        viewFactory?.invoke(parentViewGroup)

    internal fun attachChild(child: Node<*>, bundle: Bundle? = null) {
        attachChildView(child)
        // todo refactor so that this branching is not necessary
        if (bundle != null) {
            attachChildNode(child, bundle)
        } else {
            attachChildNode(child)
        }
    }

    internal fun attachChildView(child: Node<*>) {
        parentViewGroup?.let {
            child.attachToView(
                getParentViewForChild(child, view, it)
            )
        }
    }

    /**
     * todo consider a callback to [Router] with child, then falling back to this
     */
    open fun getParentViewForChild(child: Node<*>, view: V?, parentViewGroup: ViewGroup): ViewGroup =
        view?.androidView ?: parentViewGroup

    internal fun detachChild(child: Node<*>) {
        detachChildNode(child)
        detachChildView(child)
    }

    internal fun saveViewState() {
        view?.let {
            it.androidView.saveHierarchyState(savedViewState)
        }
    }

    internal fun detachChildView(child: Node<*>) {
        parentViewGroup?.let {
            child.onDetachFromView(
                parentViewGroup = getParentViewForChild(child, view, it)
            )
        }
    }

    fun onDetachFromView(parentViewGroup: ViewGroup) {
        children.forEach {
            detachChildView(
                child = it
            )
        }

        view?.let {
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
     * Called when a node is being attached. Node subclasses can perform setup here for anything
     * that is needed again but is cleaned up in willDetach(). Use didLoad() if the setup is only
     * needed once.
     */
    protected fun willAttach() {}

    /**
     * Called when a node is being a detached, node subclasses should perform any required clean
     * up here.
     */
    protected fun willDetach() {}

    /**
     * Attaches a child node to this node.
     *
     * @param childNode the [Node] to be attached.
     * @param tag an identifier to namespace saved instance state [Bundle] objects.
     */
    @MainThread
    protected fun attachChildNode(childNode: Node<*>) {
        children.add(childNode)
        ribRefWatcher.logBreadcrumb(
            "ATTACHED", childNode.javaClass.simpleName, this.javaClass.simpleName
        )
        var childBundle: Bundle? = null
        if (this.savedInstanceState != null) {
            val previousChildren = this.savedInstanceState!!.getBundle(KEY_CHILD_NODES)
            if (previousChildren != null) {
                childBundle = previousChildren.getBundle(tag)
            }
        }

        childNode.dispatchAttach(childBundle)
    }

    /**
     * Attaches a child node to this node.
     *
     * @param childNode the [Node] to be attached.
     */
    @MainThread
    protected fun attachChildNode(childNode: Node<*>, bundle: Bundle?) {
        children.add(childNode)
        ribRefWatcher.logBreadcrumb(
            "ATTACHED", childNode.javaClass.simpleName, this.javaClass.simpleName
        )

        childNode.dispatchAttach(bundle)
    }

    /**
     * Detaches the node from this parent. NOTE: No consumers of
     * this API should ever keep a reference to the detached child, leak canary will enforce
     * that it gets garbage collected.
     *
     * @param childNode the [Node] to be detached.
     */
    @MainThread
    protected fun detachChildNode(childNode: Node<*>) {
        children.remove(childNode)

        val interactor = childNode.interactor
        ribRefWatcher.watchDeletedObject(interactor)
        ribRefWatcher.logBreadcrumb(
            "DETACHED", childNode.javaClass.simpleName, this.javaClass.simpleName
        )
        if (savedInstanceState != null) {
            var childrenBundles: Bundle? = savedInstanceState!!.getBundle(KEY_CHILD_NODES)
            if (childrenBundles == null) {
                childrenBundles = Bundle()
                savedInstanceState!!.putBundle(KEY_CHILD_NODES, childrenBundles)
            }
            childrenBundles.putBundle(childNode.tag, null)
        }

        childNode.dispatchDetach()
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
            detachChildNode(child)
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
        outState.putBundle(KEY_CHILD_NODES, childBundles)
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

