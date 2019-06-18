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

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.lifecycle.LifecycleEndedException;
import com.uber.rib.core.lifecycle.ActivityCallbackEvent;
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static com.uber.autodispose.AutoDispose.autoDisposable;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.setupActivity;

@RunWith(RobolectricTestRunner.class)
public class RibActivityTest {

  private static final String TEST_BUNDLE_KEY = "test_bundle_key";
  private static final String TEST_BUNDLE_VALUE = "test_bundle_value";

  @Test
  public void onCreate_withSaveInstanceState_shouldForwardToRootRiblet() {
    android.os.Bundle interactorBundle = new android.os.Bundle();
    interactorBundle.putString(TEST_BUNDLE_KEY, TEST_BUNDLE_VALUE);

    android.os.Bundle testBundle = new android.os.Bundle();
    testBundle.putBundle(Router.KEY_INTERACTOR, interactorBundle);

    ActivityController<EmptyActivity> activityController =
        Robolectric.buildActivity(EmptyActivity.class);
    activityController.create(testBundle);

    assertThat(
            activityController
                .get()
                .getTestInteractor()
                .getSavedInstanceState()
                .getString(TEST_BUNDLE_KEY))
        .isEqualTo(TEST_BUNDLE_VALUE);
  }

  @Test
  public void onCreate_withNullSaveInstanceState_shouldForwardNullToRootRiblet() {
    ActivityController<EmptyActivity> activityController =
        Robolectric.buildActivity(EmptyActivity.class);
    activityController.create();

    assertThat(activityController.get().getTestInteractor().getSavedInstanceState()).isNull();
  }

  @Test
  public void rxActivity_shouldCallback_onLowMemory() {
    ActivityController<EmptyActivity> activityController = buildActivity(EmptyActivity.class);
    RibActivity activity = activityController.setup().get();
    TestObserver<ActivityCallbackEvent> testSub = new TestObserver<>();
    activity
        .callbacks()
        .filter(
            new Predicate<ActivityCallbackEvent>() {
              @Override
              public boolean test(ActivityCallbackEvent activityEvent) throws Exception {
                return activityEvent.getType() == ActivityCallbackEvent.Type.LOW_MEMORY;
              }
            })
        .subscribe(testSub);

    activity.onLowMemory();

    testSub.assertValue(ActivityCallbackEvent.create(ActivityCallbackEvent.Type.LOW_MEMORY));
  }

  @Test
  public void ribActivity_onSaveInstanceStateAndCallbackFlagEnabled_shouldEmitToCallbacks() {
    ActivityController<EmptyActivity> activityController = buildActivity(EmptyActivity.class);
    RibActivity activity = activityController.setup().get();
    TestObserver<ActivityCallbackEvent.SaveInstanceState> testSub = new TestObserver<>();
    activity.callbacks(ActivityCallbackEvent.SaveInstanceState.class).subscribe(testSub);

    android.os.Bundle state = new android.os.Bundle();
    state.putString("hello", "seattle");
    activity.onSaveInstanceState(state);

    testSub.assertValueCount(1);
    ActivityCallbackEvent.SaveInstanceState receivedEvent = testSub.values().get(0);
    assertThat(receivedEvent.getType()).isEqualTo(ActivityCallbackEvent.Type.SAVE_INSTANCE_STATE);
    assertThat(receivedEvent.getOutState()).isNotNull();
    assertThat(receivedEvent.getOutState().getString("hello")).isEqualTo("seattle");
  }

  @Test
  public void rxActivity_shouldCallback_onActivityResult() {
    ActivityController<EmptyActivity> activityController = buildActivity(EmptyActivity.class);
    RibActivity activity = activityController.setup().get();
    TestObserver<ActivityCallbackEvent.ActivityResult> testSub = new TestObserver<>();
    activity.callbacks(ActivityCallbackEvent.ActivityResult.class).subscribe(testSub);

    android.os.Bundle data = new android.os.Bundle();
    data.putString("hello", "seattle");
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.putExtras(data);
    int requestCode = 2;
    int resultCode = Activity.RESULT_OK;
    activity.onActivityResult(requestCode, resultCode, intent);

    testSub.assertValueCount(1);
    ActivityCallbackEvent.ActivityResult receivedEvent = testSub.values().get(0);
    assertThat(receivedEvent.getType()).isEqualTo(ActivityCallbackEvent.Type.ACTIVITY_RESULT);
    assertThat(receivedEvent.getRequestCode()).isEqualTo(requestCode);
    assertThat(receivedEvent.getResultCode()).isEqualTo(resultCode);
    assertThat(receivedEvent.getData()).isNotNull();
    assertThat(receivedEvent.getData().getExtras()).isNotNull();
    assertThat(receivedEvent.getData().getExtras().getString("hello")).isEqualTo("seattle");
  }

  @Test
  public void rxActivity_delaySubscription_shouldIgnoreOtherEvents() {
    ActivityController<EmptyActivity> activityController = buildActivity(EmptyActivity.class);
    final RibActivity activity = activityController.get();

    final PublishSubject<Object> subject = PublishSubject.create();
    AndroidRecordingRx2Observer<Object> o = new AndroidRecordingRx2Observer<>();
    subject
        .hide()
        .delaySubscription(
            activity
                .lifecycle()
                .filter(
                    new Predicate<ActivityLifecycleEvent>() {
                      @Override
                      public boolean test(ActivityLifecycleEvent activityEvent) throws Exception {
                        return activityEvent.getType() == ActivityLifecycleEvent.Type.RESUME;
                      }
                    }))
        .subscribe(o);

    subject.onNext(new Object());
    activityController.create();
    subject.onNext(new Object());
    o.assertNoMoreEvents();
    activityController.start();
    subject.onNext(new Object());
    o.assertNoMoreEvents();
    activityController.postCreate(null);
    subject.onNext(new Object());
    o.assertNoMoreEvents();
    activityController.resume();
    subject.onNext(new Object());
    assertThat(o.takeNext()).isNotNull();
    o.assertNoMoreEvents();
  }

  @Test
  public void onSaveInstanceState_shouldPropagate() {
    ActivityController<EmptyActivity> activityController =
        Robolectric.buildActivity(EmptyActivity.class);
    EmptyActivity activity = activityController.setup().get();

    android.os.Bundle bundle = new android.os.Bundle();
    activity.onSaveInstanceState(bundle);

    android.os.Bundle interactorBundle = bundle.getBundle(Router.KEY_INTERACTOR);
    assertThat(interactorBundle).isNotNull();
  }

  @Test
  public void bind_afterDestroy_shouldError() {
    ActivityController<EmptyActivity> activityController =
        Robolectric.buildActivity(EmptyActivity.class);
    EmptyActivity activity = activityController.setup().pause().stop().destroy().get();
    AndroidRecordingRx2Observer<Object> o = new AndroidRecordingRx2Observer<>();
    Observable.just(new Object()).as(autoDisposable(activity)).subscribe(o);

    assertThat(o.takeError()).isInstanceOf(LifecycleEndedException.class);
  }

  @Test
  public void getController() {
    RibActivity activity = setupActivity(EmptyActivity.class);
    assertThat(activity.getInteractor()).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void createEvent_withIllegalType_shouldFail() {
    ActivityLifecycleEvent.create(ActivityLifecycleEvent.Type.CREATE);
  }

  private static class EmptyActivity extends RibActivity {

    @NonNull
    @Override
    protected ViewRouter<?, ?, ?> createRouter(@NonNull ViewGroup parentViewGroup) {
      FrameLayout view = new FrameLayout(RuntimeEnvironment.application);

      InteractorComponent component = mock(InteractorComponent.class);
      ViewPresenter presenter = new ViewPresenter<View>(view) {};
      when(component.presenter()).thenReturn(presenter);

      return new EmptyRouter(view, new TestInteractor(), component);
    }

    TestInteractor getTestInteractor() {
      return (TestInteractor) getInteractor();
    }
  }

  private static class EmptyRouter
      extends ViewRouter<FrameLayout, Interactor<ViewPresenter, ?>, InteractorComponent> {

    EmptyRouter(
        @NonNull FrameLayout view,
        @NonNull Interactor<ViewPresenter, ?> interactor,
        @NonNull InteractorComponent<ViewPresenter, ?> component) {
      super(view, interactor, component);
      interactor.presenter = component.presenter();
    }
  }

  private static class TestInteractor extends Interactor<ViewPresenter, EmptyRouter> {

    private Bundle savedInstanceState;

    @Override
    protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
      super.didBecomeActive(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
      super.onSaveInstanceState(outState);
    }

    Bundle getSavedInstanceState() {
      return savedInstanceState;
    }
  }
}
