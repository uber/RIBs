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
package com.uber.rib.core;

import android.os.Looper;
import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.uber.rib.core.Preconditions.*;

/**
 * Responsible for handling the addition and removal of children routers.
 *
 * @param <I> type of interactor this router routes.
 * @param <C> type of dependency held by this router.
 */
public class Router<I extends com.uber.rib.core.Interactor, C extends InteractorBaseComponent> {

  @VisibleForTesting static final String KEY_CHILD_ROUTERS = "Router.childRouters";
  @VisibleForTesting static final String KEY_INTERACTOR = "Router.interactor";

  private final List<Router> children = new CopyOnWriteArrayList<>();
  private final I interactor;
  private final Thread mainThread;
  private final com.uber.rib.core.RibRefWatcher ribRefWatcher;

  private String tag;

  @Nullable private Bundle savedInstanceState;

  private boolean isLoaded;

  public Router(I interactor, C component) {
    this(component, interactor, com.uber.rib.core.RibRefWatcher.getInstance(), getMainThread());
  }

  @SuppressWarnings("unchecked")
  Router(
      C component, I interactor, com.uber.rib.core.RibRefWatcher ribRefWatcher, Thread mainThread) {
    this.interactor = interactor;
    this.ribRefWatcher = ribRefWatcher;
    this.mainThread = mainThread;
    component.inject(interactor);
    interactor.setRouter(this);
  }

  /** @return the interactor owned by the router. */
  public I getInteractor() {
    return interactor;
  }

  /**
   * Dispatch back press to the associated interactor. Do not override this.
   *
   * @return TRUE if the interactor handles the back press.
   */
  public boolean handleBackPress() {
    ribRefWatcher.logBreadcrumb("BACKPRESS", null, null);
    return getInteractor().handleBackPress();
  }

  /** Called after the router has been loaded and initialized. */
  @Initializer
  protected void didLoad() {}

  /**
   * Called when a router is being attached. Router subclasses can perform setup here for anything
   * that is needed again but is cleaned up in willDetach(). Use didLoad() if the setup is only
   * needed once.
   */
  protected void willAttach() {}

  /**
   * Called when a router is being a detached, router subclasses should perform any required clean
   * up here.
   */
  protected void willDetach() {}

  /**
   * Attaches a child router to this router. This method will automatically tag the child router by
   * its class name to namespace its saved instance state {@link Bundle} object.
   *
   * <p>If you have multiple children of the same class, use {@link Router#attachChild(Router,
   * String)} to specify a custom tag.
   *
   * @param childRouter the {@link Router} to be attached.
   */
  @MainThread
  protected void attachChild(Router<?, ?> childRouter) {
    attachChild(childRouter, childRouter.getClass().getName());
  }

  /**
   * Attaches a child router to this router.
   *
   * @param childRouter the {@link Router} to be attached.
   * @param tag an identifier to namespace saved instance state {@link Bundle} objects.
   */
  @MainThread
  protected void attachChild(Router<?, ?> childRouter, String tag) {
    for (Router child : children) {
      if (tag.equals(child.tag)) {
        Rib.getConfiguration()
            .handleNonFatalWarning(
                String.format(
                    Locale.getDefault(), "There is already a child router with tag: %s", tag),
                null);
      }
    }

    children.add(childRouter);
    ribRefWatcher.logBreadcrumb(
        "ATTACHED", childRouter.getClass().getSimpleName(), this.getClass().getSimpleName());
    Bundle childBundle = null;
    if (this.savedInstanceState != null) {
      Bundle previousChildren =
          checkNotNull(this.savedInstanceState.getBundleExtra(KEY_CHILD_ROUTERS));
      childBundle = previousChildren.getBundleExtra(tag);
    }

    childRouter.dispatchAttach(childBundle, tag);
  }

  /**
   * Detaches the {@param childFactory} from the current {@link Interactor}. NOTE: No consumers of
   * this API should ever keep a reference to the detached child router, leak canary will enforce
   * that it gets garbage collected.
   *
   * <p>If you need to keep references to previous routers, use {@link RouterNavigator}.
   *
   * @param childRouter the {@link Router} to be detached.
   */
  @MainThread
  protected void detachChild(Router childRouter) {
    children.remove(childRouter);

    Interactor interactor = childRouter.getInteractor();
    ribRefWatcher.watchDeletedObject(interactor);
    ribRefWatcher.logBreadcrumb(
        "DETACHED", childRouter.getClass().getSimpleName(), this.getClass().getSimpleName());
    if (savedInstanceState != null) {
      Bundle childrenBundles =
          checkNotNull(savedInstanceState.getBundleExtra(KEY_CHILD_ROUTERS));
      childrenBundles.putBundleExtra(childRouter.tag, null);
    }

    childRouter.dispatchDetach();
  }

  @CallSuper
  protected void dispatchAttach(@Nullable final Bundle savedInstanceState) {
    dispatchAttach(savedInstanceState, this.getClass().getName());
  }

  @CallSuper
  protected void dispatchAttach(@Nullable final Bundle savedInstanceState, String tag) {
    checkForMainThread();

    if (!isLoaded) {
      isLoaded = true;
      didLoad();
    }
    this.savedInstanceState = savedInstanceState;
    this.tag = tag;
    willAttach();

    Bundle interactorBundle = null;
    if (this.savedInstanceState != null) {
      interactorBundle = this.savedInstanceState.getBundleExtra(KEY_INTERACTOR);
    }
    getInteractor().dispatchAttach(interactorBundle);
  }

  protected void dispatchDetach() {
    checkForMainThread();

    getInteractor().dispatchDetach();
    willDetach();

    for (Router child : children) {
      detachChild(child);
    }
  }

  /**
   * Gets the children of this {@link Router}.
   *
   * @return Children.
   */
  List<Router> getChildren() {
    return children;
  }

  /**
   * Gets the tag.
   *
   * @return Tag.
   */
  String getTag() {
    return tag;
  }

  void saveInstanceState(com.uber.rib.core.Bundle outState) {
    Bundle interactorSavedInstanceState = new Bundle();
    getInteractor().onSaveInstanceState(interactorSavedInstanceState);
    outState.putBundleExtra(KEY_INTERACTOR, interactorSavedInstanceState);

    Bundle childBundles = new Bundle();
    for (Router child : children) {
      Bundle childBundle = new Bundle();
      child.saveInstanceState(childBundle);
      childBundles.putBundleExtra(child.tag, childBundle);
    }
    outState.putBundleExtra(KEY_CHILD_ROUTERS, childBundles);
  }

  private void checkForMainThread() {
    if (mainThread != Thread.currentThread()) {
      String errorMessage = "Call must happen on the main thread";
      IllegalStateException exception = new IllegalStateException(errorMessage);
      Rib.getConfiguration().handleNonFatalError(errorMessage, exception);
    }
  }

  private static Thread getMainThread() {
    try {
      return Looper.getMainLooper().getThread();
    } catch (Exception e) {
      return Thread.currentThread();
    }
  }
}
