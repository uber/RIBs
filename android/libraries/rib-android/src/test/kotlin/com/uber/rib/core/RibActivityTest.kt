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

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.common.truth.Truth.assertThat
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.rib.android.R
import com.uber.rib.core.lifecycle.ActivityCallbackEvent
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.Companion.create
import com.uber.rib.core.lifecycle.ActivityCallbackEvent.SaveInstanceState
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent.Companion.create
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class RibActivityTest {
  @Test
  fun onCreate_withSaveInstanceState_shouldForwardToRootRiblet() {
    val interactorBundle = android.os.Bundle()
    interactorBundle.putString(TEST_BUNDLE_KEY, TEST_BUNDLE_VALUE)
    val testBundle = android.os.Bundle()
    testBundle.putBundle(Router.KEY_INTERACTOR, interactorBundle)
    val activityController: ActivityController<EmptyActivity> =
      Robolectric.buildActivity(EmptyActivity::class.java)
    activityController.create(testBundle)
    assertThat(
        activityController.get().testInteractor.savedInstanceState?.getString(TEST_BUNDLE_KEY),
      )
      .isEqualTo(TEST_BUNDLE_VALUE)
  }

  @Test
  fun onCreate_withNullSaveInstanceState_shouldForwardNullToRootRiblet() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    activityController.create()
    assertThat(activityController.get().testInteractor.savedInstanceState).isNull()
  }

  @Test
  fun rxActivity_shouldCallback_onLowMemory() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity: RibActivity = activityController.setup().get()
    val testSub = TestObserver<ActivityCallbackEvent>()
    activity
      .callbacks()
      .filter { activityEvent -> activityEvent.type === ActivityCallbackEvent.Type.LOW_MEMORY }
      .subscribe(testSub)
    activity.onLowMemory()
    testSub.assertValue(create(ActivityCallbackEvent.Type.LOW_MEMORY))
  }

  @Test
  fun ribActivity_onSaveInstanceStateAndCallbackFlagEnabled_shouldEmitToCallbacks() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity: RibActivity = activityController.setup().get()
    val testSub = TestObserver<SaveInstanceState>()
    activity.callbacks(SaveInstanceState::class.java).subscribe(testSub)
    val state = android.os.Bundle()
    state.putString("hello", "seattle")
    activityController.saveInstanceState(state)
    testSub.assertValueCount(1)
    val receivedEvent = testSub.values()[0]
    assertThat(receivedEvent.type).isEqualTo(ActivityCallbackEvent.Type.SAVE_INSTANCE_STATE)
    assertThat(receivedEvent.outState).isNotNull()
    assertThat(receivedEvent.outState!!.getString("hello")).isEqualTo("seattle")
  }

  @Test
  fun rxActivity_shouldCallback_onActivityResult() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity: EmptyActivity = activityController.setup().get()
    val testSub = TestObserver<ActivityCallbackEvent.ActivityResult>()
    activity.callbacks(ActivityCallbackEvent.ActivityResult::class.java).subscribe(testSub)
    val data = android.os.Bundle()
    data.putString("hello", "seattle")
    val intent = Intent(Intent.ACTION_VIEW)
    intent.putExtras(data)
    val requestCode = 2
    val resultCode = Activity.RESULT_OK
    activity.onActivityResult(requestCode, resultCode, intent)
    testSub.assertValueCount(1)
    val receivedEvent = testSub.values()[0]
    assertThat(receivedEvent.type).isEqualTo(ActivityCallbackEvent.Type.ACTIVITY_RESULT)
    assertThat(receivedEvent.requestCode).isEqualTo(requestCode)
    assertThat(receivedEvent.resultCode).isEqualTo(resultCode)
    assertThat(receivedEvent.data).isNotNull()
    assertThat(receivedEvent.data!!.extras).isNotNull()
    assertThat(receivedEvent.data!!.extras!!.getString("hello")).isEqualTo("seattle")
  }

  @Test
  fun rxActivity_delaySubscription_shouldIgnoreOtherEvents() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity: RibActivity = activityController.get()
    val subject = PublishSubject.create<Any>()
    val o = AndroidRecordingRx2Observer<Any>()
    subject
      .hide()
      .delaySubscription(
        activity.lifecycle().filter { activityEvent ->
          activityEvent.type === ActivityLifecycleEvent.Type.RESUME
        },
      )
      .subscribe(o)
    subject.onNext(Any())
    activityController.create()
    subject.onNext(Any())
    o.assertNoMoreEvents()
    activityController.start()
    subject.onNext(Any())
    o.assertNoMoreEvents()
    activityController.postCreate(null)
    subject.onNext(Any())
    o.assertNoMoreEvents()
    activityController.resume()
    subject.onNext(Any())
    assertThat(o.takeNext()).isNotNull()
    o.assertNoMoreEvents()
  }

  @Test
  fun onSaveInstanceState_shouldPropagate() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity = activityController.setup().get()
    val bundle = android.os.Bundle()
    activityController.saveInstanceState(bundle)
    val interactorBundle = bundle.getBundle(Router.KEY_INTERACTOR)
    assertThat(interactorBundle).isNotNull()
  }

  @Test
  fun bind_afterDestroy_shouldError() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity = activityController.setup().pause().stop().destroy().get()
    val o = AndroidRecordingRx2Observer<Any>()
    Observable.just(Any()).`as`(AutoDispose.autoDisposable(activity)).subscribe(o)
    assertThat(o.takeError()).isInstanceOf(LifecycleEndedException::class.java)
  }

  @Test
  fun rxActivity_shouldCallback_onWindowFocusChanged() {
    val activityController = Robolectric.buildActivity(EmptyActivity::class.java)
    val activity: EmptyActivity = activityController.setup().get()
    val testSub = TestObserver<ActivityCallbackEvent.WindowFocus>()
    activity.callbacks(ActivityCallbackEvent.WindowFocus::class.java).subscribe(testSub)
    activity.onWindowFocusChanged(true)
    activity.onWindowFocusChanged(false)
    testSub.assertValueCount(2)
    val receivedEvent1 = testSub.values()[0]
    assertThat(receivedEvent1.type).isEqualTo(ActivityCallbackEvent.Type.WINDOW_FOCUS)
    assertThat(receivedEvent1.hasFocus).isTrue()
    val receivedEvent2 = testSub.values()[1]
    assertThat(receivedEvent2.type).isEqualTo(ActivityCallbackEvent.Type.WINDOW_FOCUS)
    assertThat(receivedEvent2.hasFocus).isFalse()
  }

  @Test
  fun getController() {
    val activity: RibActivity = Robolectric.setupActivity(EmptyActivity::class.java)
    assertThat(activity.interactor).isNotNull()
  }

  @Test(expected = IllegalArgumentException::class)
  fun createEvent_withIllegalType_shouldFail() {
    create(ActivityLifecycleEvent.Type.CREATE)
  }

  private class EmptyActivity : RibActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
      setTheme(R.style.Theme_AppCompat)
      super.onCreate(savedInstanceState)
    }

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *> {
      val view = FrameLayout(RuntimeEnvironment.application)
      val presenter = object : ViewPresenter<View>(view) {}
      val component: InteractorComponent<ViewPresenter<*>, *> = mock {
        on { presenter() } doReturn (presenter)
      }
      return EmptyRouter(view, TestInteractor(presenter), component)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
    }

    val testInteractor: TestInteractor
      get() = interactor as TestInteractor
  }

  private class EmptyRouter(
    view: FrameLayout,
    interactor: Interactor<ViewPresenter<*>, *>,
    component: InteractorComponent<ViewPresenter<*>, *>,
  ) : ViewRouter<FrameLayout, Interactor<ViewPresenter<*>, *>>(view, interactor, component)

  private class TestInteractor(
    presenter: ViewPresenter<*>,
  ) : Interactor<ViewPresenter<*>, FakeRouter<*>>(presenter) {
    var savedInstanceState: Bundle? = null
      private set

    override fun didBecomeActive(savedInstanceState: Bundle?) {
      super.didBecomeActive(savedInstanceState)
      this.savedInstanceState = savedInstanceState
    }

    override fun onSaveInstanceState(outState: Bundle) {
      super.onSaveInstanceState(outState)
    }
  }

  companion object {
    private const val TEST_BUNDLE_KEY = "test_bundle_key"
    private const val TEST_BUNDLE_VALUE = "test_bundle_value"
  }
}
