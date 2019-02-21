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
import android.support.annotation.MainThread
import android.support.annotation.VisibleForTesting
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Responsible for handling the addition and removal of child routers.
 **/
open class Router<V : RibView>(
    val interactor: Interactor<V>,
    private val ribRefWatcher: RibRefWatcher = RibRefWatcher.getInstance()
) {
    companion object {
        @VisibleForTesting internal val KEY_CHILD_ROUTERS = "Router.childRouters"
        @VisibleForTesting internal val KEY_INTERACTOR = "Router.interactor"
    }

    private var savedInstanceState: Bundle? = null
    val children = CopyOnWriteArrayList<Router<*>>()
    var tag: String? = null
        private set

    init {
        interactor.router = this
    }

    /**
     * Dispatch back press to the associated interactor. Do not override this.
     *
     * @return TRUE if the interactor handles the back press.
     */
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
    @JvmOverloads
    protected fun attachChild(
        childRouter: Router<*>,
        tag: String = childRouter.javaClass.name
    ) {
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

        childRouter.dispatchAttach(childBundle, tag)
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
    fun dispatchAttach(savedInstanceState: Bundle?) {
        dispatchAttach(savedInstanceState, this.javaClass.getName())
    }

    @CallSuper
    open fun dispatchAttach(savedInstanceState: Bundle?, tag: String) {
        this.savedInstanceState = savedInstanceState
        this.tag = tag
        willAttach()

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
        val interactorSavedInstanceState = Bundle()
        interactor.onSaveInstanceState(interactorSavedInstanceState)
        outState.putBundle(KEY_INTERACTOR, interactorSavedInstanceState)

        val childBundles = Bundle()
        for (child in children) {
            Bundle().let {
                child.saveInstanceState(it)
                childBundles.putBundle(child.tag, it)
            }

        }
        outState.putBundle(KEY_CHILD_ROUTERS, childBundles)
    }
}

