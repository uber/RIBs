/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core

import android.os.Looper
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Responsible for handling the addition and removal of children routers.
 *
 * @param <I> type of interactor this router routes.
 */
public abstract class Router<I : InteractorType>
protected constructor(
  component: InteractorBaseComponent<*>?,
  public open val interactor: I,
  private val ribRefWatcher: RibRefWatcher,
  private val mainThread: Thread,
) {
  private val children: MutableList<Router<*>> = CopyOnWriteArrayList()
  private val interactorGeneric: Interactor<*, *>
    get() = interactor as Interactor<*, *>

  /** @return the Tag. */
  internal var tag: String? = null
    private set
  private var savedInstanceState: Bundle? = null
  private var isLoaded = false

  protected constructor(
    interactor: I,
    component: InteractorBaseComponent<*>?,
  ) : this(component, interactor, RibRefWatcher.getInstance(), getMainThread())
  protected constructor(
    interactor: I,
  ) : this(null, interactor, RibRefWatcher.getInstance(), getMainThread())

  @Suppress("UNCHECKED_CAST")
  protected fun inject(component: InteractorBaseComponent<*>?) {
    (component as? InteractorBaseComponent<Interactor<*, *>>)?.inject(interactorGeneric)
  }

  protected open fun attachToInteractor() {
    interactorGeneric.setRouterInternal(this)
  }

  /**
   * Dispatch back press to the associated interactor. Do not override this.
   *
   * @return TRUE if the interactor handles the back press.
   */
  public open fun handleBackPress(): Boolean {
    ribRefWatcher.logBreadcrumb("BACKPRESS", null, null)
    return interactorGeneric.handleBackPress()
  }

  /** Called after the router has been loaded and initialized. */
  @Initializer protected open fun didLoad() {}

  /**
   * Called when a router is being attached. Router subclasses can perform setup here for anything
   * that is needed again but is cleaned up in willDetach(). Use didLoad() if the setup is only
   * needed once.
   */
  protected open fun willAttach() {}

  /**
   * Called when a router is being a detached, router subclasses should perform any required clean
   * up here.
   */
  protected open fun willDetach() {}

  @MainThread
  public open fun attachChild(childRouter: Router<*>) {
    attachChild(childRouter, childRouter.javaClass.name)
  }

  /**
   * Attaches a child router to this router.
   *
   * @param childRouter the [Router] to be attached.
   * @param tag an identifier to namespace saved instance state [Bundle] objects.
   */
  @MainThread
  public open fun attachChild(childRouter: Router<*>, tag: String) {
    for (child in children) {
      if (tag == child.tag) {
        Rib.getConfiguration()
          .handleNonFatalWarning(
            String.format(
              Locale.getDefault(),
              "There is already a child router with tag: %s",
              tag,
            ),
            null,
          )
      }
    }
    children.add(childRouter)
    ribRefWatcher.logBreadcrumb(
      "ATTACHED",
      childRouter.javaClass.simpleName,
      this.javaClass.simpleName,
    )
    RibEvents.getInstance().emitEvent(RibEventType.ATTACHED, childRouter, this)
    var childBundle: Bundle? = null
    if (savedInstanceState != null) {
      val previousChildren = savedInstanceState?.getBundleExtra(KEY_CHILD_ROUTERS)
      childBundle = previousChildren?.getBundleExtra(tag)
    }
    childRouter.dispatchAttach(childBundle, tag)
  }

  /**
   * Detaches the {@param childFactory} from the current [Interactor]. NOTE: No consumers of this
   * API should ever keep a reference to the detached child router, leak canary will enforce that it
   * gets garbage collected.
   *
   * If you need to keep references to previous routers, use RouterNavigator.
   *
   * @param childRouter the [Router] to be detached.
   */
  @MainThread
  public open fun detachChild(childRouter: Router<*>) {
    val isChildRemoved = children.remove(childRouter)
    val interactor = childRouter.interactor
    ribRefWatcher.watchDeletedObject(interactor)
    ribRefWatcher.logBreadcrumb(
      "DETACHED",
      childRouter.javaClass.simpleName,
      this.javaClass.simpleName,
    )
    if (savedInstanceState != null) {
      val childrenBundles = savedInstanceState?.getBundleExtra(KEY_CHILD_ROUTERS)
      val childRouterTag = childRouter.tag
      if (childRouterTag != null) {
        childrenBundles?.putBundleExtra(childRouterTag, null)
      } else {
        Rib.getConfiguration()
          .handleNonFatalWarning("A RIB tried to detach a child that was never attached", null)
      }
    }
    childRouter.dispatchDetach()
    if (isChildRemoved) {
      RibEvents.getInstance().emitEvent(RibEventType.DETACHED, childRouter, this)
    }
  }

  @CallSuper
  public open fun dispatchAttach(savedInstanceState: Bundle?) {
    dispatchAttach(savedInstanceState, javaClass.name)
  }

  @CallSuper
  @Initializer
  public open fun dispatchAttach(savedInstanceState: Bundle?, tag: String) {
    checkForMainThread()
    if (!isLoaded) {
      isLoaded = true
      didLoad()
    }
    this.savedInstanceState = savedInstanceState
    this.tag = tag
    willAttach()
    var interactorBundle: Bundle? = null
    if (this.savedInstanceState != null) {
      interactorBundle = this.savedInstanceState!!.getBundleExtra(KEY_INTERACTOR)
    }
    interactorGeneric.dispatchAttach(interactorBundle)
  }

  public open fun dispatchDetach() {
    checkForMainThread()

    interactorGeneric.dispatchDetach()
    willDetach()
    for (child in children) {
      detachChild(child)
    }
  }

  /**
   * Gets the children of this [Router].
   *
   * @return Children.
   */
  internal open fun getChildren(): List<Router<*>> {
    return children
  }

  internal fun saveInstanceStateInternal(outState: Bundle) {
    saveInstanceState(outState)
  }

  protected open fun saveInstanceState(outState: Bundle) {
    val interactorSavedInstanceState = Bundle()
    interactorGeneric.onSaveInstanceStateInternal(interactorSavedInstanceState)
    outState.putBundleExtra(KEY_INTERACTOR, interactorSavedInstanceState)
    val childBundles = Bundle()
    for (child in children) {
      val childBundle = Bundle()
      child.saveInstanceState(childBundle)
      childBundles.putBundleExtra(child.tag!!, childBundle)
    }
    outState.putBundleExtra(KEY_CHILD_ROUTERS, childBundles)
  }

  private fun checkForMainThread() {
    if (mainThread !== Thread.currentThread()) {
      val errorMessage = "Call must happen on the main thread"
      val exception = IllegalStateException(errorMessage)
      Rib.getConfiguration().handleNonFatalError(errorMessage, exception)
    }
  }

  public companion object {
    @VisibleForTesting public val KEY_CHILD_ROUTERS: String = "Router.childRouters"

    @JvmField @VisibleForTesting public val KEY_INTERACTOR: String = "Router.interactor"

    @JvmStatic
    public fun getMainThread(): Thread {
      return try {
        Looper.getMainLooper().thread
      } catch (e: Exception) {
        Thread.currentThread()
      }
    }
  }

  init {
    inject(component)
    attachToInteractor()
  }
}
