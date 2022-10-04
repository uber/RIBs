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

import android.R
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.uber.autodispose.lifecycle.LifecycleScopes
import com.uber.rib.core.lifecycle.ActivityCallbackEvent
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.create
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.createNewIntent
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.createOnActivityResultEvent
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.createOnSaveInstanceStateEvent
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.createPictureInPictureMode
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.createTrimMemoryEvent
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.createWindowFocusEvent
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent.Companion.create
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent.Companion.createOnCreateEvent
import io.reactivex.CompletableSource
import io.reactivex.Observable

/** Base implementation for all VIP [android.app.Activity]s. */
abstract class RibActivity : CoreAppCompatActivity(), ActivityStarter, LifecycleScopeProvider<ActivityLifecycleEvent>, RxActivityEvents {
  private var router: ViewRouter<*, *>? = null
  private val lifecycleBehaviorRelay = BehaviorRelay.create<ActivityLifecycleEvent>()
  private val lifecycleRelay: Relay<ActivityLifecycleEvent> = lifecycleBehaviorRelay.toSerialized()
  private val callbacksRelay = PublishRelay.create<ActivityCallbackEvent>().toSerialized()

  /** @return an observable of this activity's lifecycle events. */
  override fun lifecycle(): Observable<ActivityLifecycleEvent> {
    return lifecycleRelay.hide()
  }

  /** @return an observable of this activity's lifecycle events. */
  override fun callbacks(): Observable<ActivityCallbackEvent> {
    return callbacksRelay.hide()
  }

  override fun correspondingEvents(): CorrespondingEventsFunction<ActivityLifecycleEvent> {
    return ACTIVITY_LIFECYCLE
  }

  override fun peekLifecycle(): ActivityLifecycleEvent {
    return lifecycleBehaviorRelay.value!!
  }

  override fun requestScope(): CompletableSource {
    return LifecycleScopes.resolveScopeFromLifecycle(this)
  }

  @Initializer
  @CallSuper
  override fun onCreate(savedInstanceState: android.os.Bundle?) {
    super.onCreate(savedInstanceState)
    val rootViewGroup = findViewById<ViewGroup>(R.id.content)
    lifecycleRelay.accept(createOnCreateEvent(savedInstanceState))
    val wrappedBundle: Bundle? = if (savedInstanceState != null) Bundle(savedInstanceState) else null
    router = createRouter(rootViewGroup)
    router?.let {
      it.dispatchAttach(wrappedBundle)
      rootViewGroup.addView(it.view)
      RibEvents.getInstance().emitEvent(RibEventType.ATTACHED, it, null)
    }
  }

  @CallSuper
  override fun onSaveInstanceState(outState: android.os.Bundle) {
    super.onSaveInstanceState(outState)
    callbacksRelay.accept(createOnSaveInstanceStateEvent(outState))
    router?.saveInstanceStateInternal(Bundle(outState)) ?: throw NullPointerException("Router should not be null")
  }

  @CallSuper
  override fun onStart() {
    super.onStart()
    lifecycleRelay.accept(create(ActivityLifecycleEvent.Type.START))
  }

  @CallSuper
  override fun onResume() {
    super.onResume()
    lifecycleRelay.accept(create(ActivityLifecycleEvent.Type.RESUME))
  }

  @CallSuper
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    callbacksRelay.accept(createNewIntent(intent))
  }

  @CallSuper
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    callbacksRelay.accept(createOnActivityResultEvent(requestCode, resultCode, data))
  }

  @CallSuper
  override fun onPause() {
    lifecycleRelay.accept(create(ActivityLifecycleEvent.Type.PAUSE))
    super.onPause()
  }

  @CallSuper
  override fun onStop() {
    lifecycleRelay.accept(create(ActivityLifecycleEvent.Type.STOP))
    super.onStop()
  }

  @CallSuper
  override fun onDestroy() {
    lifecycleRelay.accept(create(ActivityLifecycleEvent.Type.DESTROY))
    router?.let {
      it.dispatchDetach()
      RibEvents.getInstance().emitEvent(RibEventType.DETACHED, it, null)
    }
    router = null
    super.onDestroy()
  }

  @CallSuper
  override fun onLowMemory() {
    super.onLowMemory()
    callbacksRelay.accept(create(ActivityCallbackEvent.Type.LOW_MEMORY))
  }

  @CallSuper
  override fun onTrimMemory(level: Int) {
    super.onTrimMemory(level)
    callbacksRelay.accept(createTrimMemoryEvent(level))
  }

  override fun onPictureInPictureModeChanged(
    isInPictureInPictureMode: Boolean,
    newConfig: Configuration
  ) {
    callbacksRelay.accept(
      createPictureInPictureMode(isInPictureInPictureMode)
    )
  }

  override fun onBackPressed() {
    if (router?.handleBackPress() != true) {
      onUnhandledBackPressed()

      // https://issuetracker.google.com/issues/139738913
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isTaskRoot &&
        supportFragmentManager.backStackEntryCount == 0
      ) {
        super.finishAfterTransition()
      } else {
        super.onBackPressed()
      }
    }
  }

  override fun onUserLeaveHint() {
    lifecycleRelay.accept(create(ActivityLifecycleEvent.Type.USER_LEAVING))
    super.onUserLeaveHint()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    callbacksRelay.accept(createWindowFocusEvent(hasFocus))
  }

  /**
   * Invoked when none of the ribs handle back press. In this case, default activity back press
   * behavior occurs.
   */
  protected open fun onUnhandledBackPressed() {}

  /**
   * @return the [Interactor] when the activity has alive.
   * @throws IllegalStateException if the activity has not been created or has been destroyed.
   */
  open val interactor: Interactor<*, *>
    get() = if (router != null) {
      router?.interactor as Interactor<*, *>
    } else {
      throw IllegalStateException(
        "Attempting to get a router when activity is not created or has been destroyed."
      )
    }

  /**
   * Creates the [Interactor].
   *
   * @return the [Interactor].
   */
  protected abstract fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *>

  companion object {
    /**
     * Figures out which corresponding next lifecycle event in which to unsubscribe, for Activities.
     */
    private val ACTIVITY_LIFECYCLE = CorrespondingEventsFunction { lastEvent: ActivityLifecycleEvent ->
      return@CorrespondingEventsFunction when (lastEvent.type) {
        ActivityLifecycleEvent.Type.CREATE -> create(ActivityLifecycleEvent.Type.DESTROY)
        ActivityLifecycleEvent.Type.START -> create(ActivityLifecycleEvent.Type.STOP)
        ActivityLifecycleEvent.Type.RESUME -> create(ActivityLifecycleEvent.Type.PAUSE)
        ActivityLifecycleEvent.Type.USER_LEAVING -> create(ActivityLifecycleEvent.Type.DESTROY)
        ActivityLifecycleEvent.Type.PAUSE -> create(ActivityLifecycleEvent.Type.STOP)
        ActivityLifecycleEvent.Type.STOP -> create(ActivityLifecycleEvent.Type.DESTROY)
        ActivityLifecycleEvent.Type.DESTROY -> throw LifecycleEndedException(
          "Cannot bind to Activity lifecycle when outside of it."
        )
      }
    }
  }
}
