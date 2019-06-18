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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uber.autodispose.lifecycle.LifecycleEndedException;
import com.uber.rib.core.lifecycle.InteractorEvent;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InteractorAndRouterTest {

  private static final String TEST_KEY = "test_key";
  private static final String TEST_VALUE = "test_value";

  @Mock private Interactor childInteractor;
  @Mock private RibRefWatcher ribRefWatcher;

  private TestInteractor interactor;
  private TestRouter router;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    InteractorComponent<TestPresenter, TestInteractor> component = mock(InteractorComponent.class);
    when(component.presenter()).thenReturn(mock(TestPresenter.class));

    interactor = new TestInteractor(childInteractor);
    router = new TestRouter(interactor, component);
  }

  @Test
  public void attach_shouldAttachChildController() {
    // When.
    router.dispatchAttach(null);

    // Then.
    verify(childInteractor).dispatchAttach(null);
  }

  @Test
  public void detach_shouldDetachChildController() {
    // Given.
    router.dispatchAttach(null);

    // When.
    router.dispatchDetach();

    // Then.
    verify(childInteractor).dispatchDetach();
  }

  @Ignore
  @Test
  public void saveInstanceState_whenAttached_shouldSaveChildControllerState() {
    // Given.
    TestRouterA childRouter =
        new TestRouterA(
            new TestInteractorA(),
            new InteractorComponent<TestPresenter, TestInteractorA>() {
              @Override
              public void inject(TestInteractorA interactor) {}

              @Override
              public TestPresenter presenter() {
                return new TestPresenter();
              }
            });
    router.dispatchAttach(null);
    router.attachChild(childRouter);

    // When.
    Bundle outState = new Bundle();
    router.saveInstanceState(outState);

    // Then.
    verify(childInteractor).onSaveInstanceState(any(Bundle.class));

    Bundle childrenBundle = outState.getBundleExtra(Router.KEY_CHILD_ROUTERS);
    assertThat(childrenBundle).isNotNull();
    Bundle childBundle = childrenBundle.getBundleExtra(childRouter.getClass().getName());
    assertThat(childBundle).isNotNull();

    Bundle interactorBundle = outState.getBundleExtra(Router.KEY_INTERACTOR);
    assertThat(interactorBundle.getString(TEST_KEY)).isEqualTo(TEST_VALUE);
  }

  @Test
  public void correspondingEvents_whenActive_shouldReturnInactive() throws Exception {
    assertThat(interactor.correspondingEvents().apply(InteractorEvent.ACTIVE))
        .isEqualTo(InteractorEvent.INACTIVE);
  }

  @Test(expected = LifecycleEndedException.class)
  public void correspondingEvents_whenInactive_shouldCrash() throws Exception {
    interactor.correspondingEvents().apply(InteractorEvent.INACTIVE);
  }

  @Ignore
  @Test
  public void childRouter_whenDetachedAfterReattached_shouldClearOutChildsSavedInstanceState() {
    // Given.
    TestRouterA childRouter =
        new TestRouterA(
            new TestInteractorA(),
            new InteractorComponent<TestPresenter, TestInteractorA>() {
              @Override
              public void inject(TestInteractorA interactor) {}

              @Override
              public TestPresenter presenter() {
                return new TestPresenter();
              }
            });
    router.dispatchAttach(null);
    router.attachChild(childRouter);

    // When.
    Bundle outState = new Bundle();
    router.saveInstanceState(outState);
    router.detachChild(childRouter);
    router.attachChild(childRouter);

    // Then.
    assertThat(childRouter.savedInstanceState).isNull();
  }

  @Test
  public void saveInstanceState_whenDetached_shouldNotSaveChildControllerState() {
    // When.
    com.uber.rib.core.Bundle outState = mock(com.uber.rib.core.Bundle.class);
    interactor.onSaveInstanceState(outState);

    // Then.
    verify(childInteractor, times(0)).onSaveInstanceState(outState);
  }

  @Test
  public void childControllers_shouldHaveRightLifecycle() {
    TestInteractorA parentInteractor = new TestInteractorA();
    InteractorComponent<TestPresenter, TestInteractorA> component =
        new InteractorComponent<TestPresenter, TestInteractorA>() {

          @Override
          public void inject(TestInteractorA interactor) {}

          @Override
          public TestPresenter presenter() {
            return new TestPresenter();
          }
        };

    TestRouterA router = new TestRouterA(parentInteractor, component);

    RecordingObserver<InteractorEvent> parentObserver = new RecordingObserver<>();
    parentInteractor.lifecycle().subscribe(parentObserver);

    router.dispatchAttach(null);
    assertThat(parentObserver.takeNext()).isEqualTo(InteractorEvent.ACTIVE);

    final TestChildInteractor childA = new TestChildInteractor();

    final InteractorComponent<TestPresenter, TestChildInteractor> childComponent =
        new InteractorComponent<TestPresenter, TestChildInteractor>() {
          @Override
          public void inject(TestChildInteractor interactor) {}

          @Override
          public TestPresenter presenter() {
            return new TestPresenter();
          }
        };
    final TestChildRouter childRouter = new TestChildRouter(childA, childComponent);
    RecordingObserver<InteractorEvent> childObserverA = new RecordingObserver<>();
    childA.lifecycle().subscribe(childObserverA);
    router.attachChild(childRouter);

    assertThat(childObserverA.takeNext()).isEqualTo(InteractorEvent.ACTIVE);

    final TestChildInteractor childB = new TestChildInteractor();
    RecordingObserver<InteractorEvent> childObserverB = new RecordingObserver<>();
    childB.lifecycle().subscribe(childObserverB);

    TestChildRouter childBRouter = new TestChildRouter(childB, childComponent);
    childRouter.attachChild(childBRouter);

    assertThat(childObserverB.takeNext()).isEqualTo(InteractorEvent.ACTIVE);

    router.dispatchDetach();
    assertThat(parentObserver.takeNext()).isEqualTo(InteractorEvent.INACTIVE);
    assertThat(childObserverA.takeNext()).isEqualTo(InteractorEvent.INACTIVE);
    assertThat(childObserverB.takeNext()).isEqualTo(InteractorEvent.INACTIVE);
  }

  @Test
  public void detachChild_whenOneChild_shouldWatchOneDeletedInteractor() {
    TestInteractorB rootInteractor = new TestInteractorB();
    InteractorComponent<TestPresenter, TestInteractorB> component =
        new InteractorComponent<TestPresenter, TestInteractorB>() {
          @Override
          public void inject(TestInteractorB interactor) {}

          @Override
          public TestPresenter presenter() {
            return new TestPresenter();
          }
        };
    TestRouterB router = new TestRouterB(component, rootInteractor, ribRefWatcher);
    router.dispatchAttach(null);

    TestInteractorB childInteractor = new TestInteractorB();
    TestRouterB childRouter = new TestRouterB(childInteractor, component);
    router.attachChild(childRouter);

    verify(ribRefWatcher, never()).watchDeletedObject(anyObject());

    // Action: Detach the child interactor.
    router.detachChild(childRouter);

    // Verify: the reference watcher observes the detached interactor and child.
    verify(ribRefWatcher).watchDeletedObject(eq(childInteractor));
  }

  @Test
  public void detachChild_whenTwoNestedChildren_shouldWatchTwoNestedDeletions() {
    InteractorComponent<TestPresenter, TestInteractorB> component =
        new InteractorComponent<TestPresenter, TestInteractorB>() {
          @Override
          public void inject(TestInteractorB interactor) {}

          @Override
          public TestPresenter presenter() {
            return new TestPresenter();
          }
        };

    TestRouterB rootRouter = new TestRouterB(component, new TestInteractorB(), ribRefWatcher);

    Router<TestInteractorB, ?> child = addTwoNestedChildInteractors();
    verify(ribRefWatcher, never()).watchDeletedObject(anyObject());

    // Action: Detach all child interactors.
    rootRouter.detachChild(child);

    // Verify: called four times. Twice for each interactor.
    verify(ribRefWatcher, times(2)).watchDeletedObject(anyObject());
  }

  private Router<TestInteractorB, ?> addTwoNestedChildInteractors() {
    InteractorComponent<TestPresenter, TestInteractorB> component =
        new InteractorComponent<TestPresenter, TestInteractorB>() {
          @Override
          public void inject(TestInteractorB interactor) {}

          @Override
          public TestPresenter presenter() {
            return new TestPresenter();
          }
        };

    router.dispatchAttach(null);

    TestRouterB childRouter1 = new TestRouterB(component, new TestInteractorB(), ribRefWatcher);
    TestRouterB childRouter2 = new TestRouterB(component, new TestInteractorB(), ribRefWatcher);

    router.attachChild(childRouter1);
    childRouter1.attachChild(childRouter2);

    return childRouter1;
  }

  private static class TestInteractor
      extends Interactor<
          TestPresenter,
          Router<TestInteractor, InteractorComponent<TestPresenter, TestInteractor>>> {

    @NonNull private final com.uber.rib.core.Interactor mChildInteractor;

    TestInteractor(@NonNull com.uber.rib.core.Interactor childInteractor) {
      mChildInteractor = childInteractor;
    }

    @Override
    protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
      super.didBecomeActive(savedInstanceState);

      com.uber.rib.core.Router router =
          new Router<>(
              mock(InteractorComponent.class),
              mChildInteractor,
              RibRefWatcher.getInstance(),
              Thread.currentThread());
      getRouter().attachChild(router);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putString(TEST_KEY, TEST_VALUE);
    }
  }

  private static class TestRouter
      extends Router<TestInteractor, InteractorComponent<TestPresenter, TestInteractor>> {

    TestRouter(
        @NonNull TestInteractor interactor,
        @NonNull InteractorComponent<TestPresenter, TestInteractor> component) {
      super(component, interactor, RibRefWatcher.getInstance(), Thread.currentThread());
      interactor.presenter = component.presenter();
    }
  }

  private static class TestPresenter extends com.uber.rib.core.Presenter {}

  private static class TestRouterA
      extends Router<TestInteractorA, InteractorComponent<TestPresenter, TestInteractorA>> {

    @Nullable private Bundle savedInstanceState;

    TestRouterA(
        @NonNull TestInteractorA interactor,
        @NonNull InteractorComponent<TestPresenter, TestInteractorA> component) {
      super(component, interactor, RibRefWatcher.getInstance(), Thread.currentThread());
      interactor.presenter = component.presenter();
    }

    @Override
    protected void dispatchAttach(@Nullable Bundle savedInstanceState, @NonNull String tag) {
      super.dispatchAttach(savedInstanceState, tag);
      this.savedInstanceState = savedInstanceState;
    }
  }

  private static class TestInteractorA
      extends com.uber.rib.core.Interactor<
          TestPresenter,
          com.uber.rib.core.Router<
              TestInteractorA, InteractorComponent<TestPresenter, TestInteractorA>>> {}

  private static class TestInteractorB
      extends Interactor<
          TestPresenter,
          Router<TestInteractorB, InteractorComponent<TestPresenter, TestInteractorB>>> {}

  private static class TestRouterB
      extends Router<TestInteractorB, InteractorComponent<TestPresenter, TestInteractorB>> {

    TestRouterB(
        @NonNull TestInteractorB interactor,
        @NonNull InteractorComponent<TestPresenter, TestInteractorB> component) {
      super(component, interactor, RibRefWatcher.getInstance(), Thread.currentThread());
      interactor.presenter = component.presenter();
    }

    TestRouterB(
        @NonNull InteractorComponent<TestPresenter, TestInteractorB> component,
        @NonNull TestInteractorB interactor,
        @NonNull RibRefWatcher ribRefWatcher) {
      super(component, interactor, ribRefWatcher, Thread.currentThread());
      interactor.presenter = component.presenter();
    }
  }

  private static class TestChildInteractor
      extends Interactor<
          TestPresenter,
          Router<TestChildInteractor, InteractorComponent<TestPresenter, TestChildInteractor>>> {}

  private static class TestChildRouter
      extends Router<TestChildInteractor, InteractorComponent<TestPresenter, TestChildInteractor>> {

    TestChildRouter(
        @NonNull TestChildInteractor interactor,
        @NonNull InteractorComponent<TestPresenter, TestChildInteractor> component) {
      super(component, interactor, RibRefWatcher.getInstance(), Thread.currentThread());
      interactor.presenter = component.presenter();
    }
  }
}
