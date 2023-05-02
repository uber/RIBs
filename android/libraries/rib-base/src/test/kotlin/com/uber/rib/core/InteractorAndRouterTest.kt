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

import com.google.common.truth.Truth
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.rib.core.RibRefWatcher.Companion.getInstance
import com.uber.rib.core.lifecycle.InteractorEvent
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class InteractorAndRouterTest {

  private val childInteractor: Interactor<*, *> = mock()
  private val ribRefWatcher: RibRefWatcher = mock()

  private lateinit var interactor: TestInteractor
  private lateinit var router: TestRouter

  @Before
  fun setup() {
    val presenter: TestPresenter = mock()
    val component: InteractorComponent<TestPresenter, TestInteractor> = mock {
      on { presenter() } doReturn (presenter)
    }
    interactor = TestInteractor(childInteractor)
    router = TestRouter(interactor, component)
  }

  @Test
  fun attach_shouldAttachChildController() {
    // When.
    router.dispatchAttach(null)

    // Then.
    verify(childInteractor).dispatchAttach(null)
  }

  @Test
  fun detach_shouldDetachChildController() {
    // Given.
    router.dispatchAttach(null)

    // When.
    router.dispatchDetach()

    // Then.
    verify(childInteractor).dispatchDetach()
  }

  @Test
  @Throws(Exception::class)
  fun correspondingEvents_whenActive_shouldReturnInactive() {
    Truth.assertThat(interactor.correspondingEvents().apply(InteractorEvent.ACTIVE))
      .isEqualTo(InteractorEvent.INACTIVE)
  }

  @Test(expected = LifecycleEndedException::class)
  @Throws(Exception::class)
  fun correspondingEvents_whenInactive_shouldCrash() {
    interactor.correspondingEvents().apply(InteractorEvent.INACTIVE)
  }

  @Test
  fun saveInstanceState_whenDetached_shouldNotSaveChildControllerState() {
    // When.
    val outState: Bundle = mock()
    interactor.onSaveInstanceStateInternal(outState)

    // Then.
    verify(childInteractor, times(0)).onSaveInstanceStateInternal(outState)
  }

  @Test
  fun childControllers_shouldHaveRightLifecycle() {
    val parentInteractor = TestInteractorA()
    val component: InteractorComponent<TestPresenter, TestInteractorA> =
      object : InteractorComponent<TestPresenter, TestInteractorA> {
        override fun inject(interactor: TestInteractorA) {}
        override fun presenter(): TestPresenter {
          return TestPresenter()
        }
      }
    val router = TestRouterA(parentInteractor, component)
    val parentObserver = RecordingObserver<InteractorEvent>()
    parentInteractor.lifecycle().subscribe(parentObserver)
    router.dispatchAttach(null)
    Truth.assertThat(parentObserver.takeNext()).isEqualTo(InteractorEvent.ACTIVE)
    val childA = TestChildInteractor()
    val childComponent: InteractorComponent<TestPresenter, TestChildInteractor> =
      object : InteractorComponent<TestPresenter, TestChildInteractor> {
        override fun inject(interactor: TestChildInteractor) {}
        override fun presenter(): TestPresenter {
          return TestPresenter()
        }
      }
    val childRouter = TestChildRouter(childA, childComponent)
    val childObserverA = RecordingObserver<InteractorEvent>()
    childA.lifecycle().subscribe(childObserverA)
    router.attachChild(childRouter)
    Truth.assertThat(childObserverA.takeNext()).isEqualTo(InteractorEvent.ACTIVE)
    val childB = TestChildInteractor()
    val childObserverB = RecordingObserver<InteractorEvent>()
    childB.lifecycle().subscribe(childObserverB)
    val childBRouter = TestChildRouter(childB, childComponent)
    childRouter.attachChild(childBRouter)
    Truth.assertThat(childObserverB.takeNext()).isEqualTo(InteractorEvent.ACTIVE)
    router.dispatchDetach()
    Truth.assertThat(parentObserver.takeNext()).isEqualTo(InteractorEvent.INACTIVE)
    Truth.assertThat(childObserverA.takeNext()).isEqualTo(InteractorEvent.INACTIVE)
    Truth.assertThat(childObserverB.takeNext()).isEqualTo(InteractorEvent.INACTIVE)
  }

  @Test
  fun detachChild_whenOneChild_shouldWatchOneDeletedInteractor() {
    val rootInteractor = TestInteractorB()
    val component: InteractorComponent<TestPresenter, TestInteractorB> =
      object : InteractorComponent<TestPresenter, TestInteractorB> {
        override fun inject(interactor: TestInteractorB) {}
        override fun presenter(): TestPresenter {
          return TestPresenter()
        }
      }
    val router = TestRouterB(component, rootInteractor, ribRefWatcher)
    router.dispatchAttach(null)
    val childInteractor = TestInteractorB()
    val childRouter = TestRouterB(childInteractor, component)
    router.attachChild(childRouter)
    verify(ribRefWatcher, never()).watchDeletedObject(any())

    // Action: Detach the child interactor.
    router.detachChild(childRouter)

    // Verify: the reference watcher observes the detached interactor and child.
    verify(ribRefWatcher).watchDeletedObject(eq(childInteractor))
  }

  @Test
  fun detachChild_whenTwoNestedChildren_shouldWatchTwoNestedDeletions() {
    val component: InteractorComponent<TestPresenter, TestInteractorB> =
      object : InteractorComponent<TestPresenter, TestInteractorB> {
        override fun inject(interactor: TestInteractorB) {}
        override fun presenter(): TestPresenter {
          return TestPresenter()
        }
      }
    val rootRouter = TestRouterB(component, TestInteractorB(), ribRefWatcher)
    val child = addTwoNestedChildInteractors()
    verify(ribRefWatcher, never()).watchDeletedObject(any())

    // Action: Detach all child interactors.
    rootRouter.detachChild(child)

    // Verify: called four times. Twice for each interactor.
    verify(ribRefWatcher, times(2)).watchDeletedObject(any())
  }

  private fun addTwoNestedChildInteractors(): Router<TestInteractorB> {
    val component: InteractorComponent<TestPresenter, TestInteractorB> =
      object : InteractorComponent<TestPresenter, TestInteractorB> {
        override fun inject(interactor: TestInteractorB) {}
        override fun presenter(): TestPresenter {
          return TestPresenter()
        }
      }
    router.dispatchAttach(null)
    val childRouter1 = TestRouterB(component, TestInteractorB(), ribRefWatcher)
    val childRouter2 = TestRouterB(component, TestInteractorB(), ribRefWatcher)
    router.attachChild(childRouter1)
    childRouter1.attachChild(childRouter2)
    return childRouter1
  }

  private class TestInteractor(private val mChildInteractor: Interactor<*, *>) :
    Interactor<TestPresenter, Router<TestInteractor>>() {
    val mockedWorker: Worker = mock()
    override fun didBecomeActive(savedInstanceState: Bundle?) {
      super.didBecomeActive(savedInstanceState)
      val router: Router<*> = FakeRouter(mChildInteractor, getInstance(), Thread.currentThread())
      WorkerBinder.bind(this, mockedWorker)
      this.router.attachChild(router)
    }

    override fun onSaveInstanceState(outState: Bundle) {
      super.onSaveInstanceState(outState)
      outState.putString(TEST_KEY, TEST_VALUE)
    }
  }

  private class TestRouter(
    interactor: TestInteractor,
    component: InteractorComponent<TestPresenter, TestInteractor>,
  ) : Router<TestInteractor>(component, interactor, getInstance(), Thread.currentThread()) {
    init {
      interactor.setPresenter(component.presenter())
    }
  }

  private open class TestPresenter : Presenter()

  private class TestRouterA(
    interactor: TestInteractorA,
    component: InteractorComponent<TestPresenter, TestInteractorA>,
  ) : Router<TestInteractorA>(component, interactor, getInstance(), Thread.currentThread()) {
    private var savedInstanceState: Bundle? = null
    override fun dispatchAttach(savedInstanceState: Bundle?, tag: String) {
      super.dispatchAttach(savedInstanceState, tag)
      this.savedInstanceState = savedInstanceState
    }

    init {
      interactor.setPresenter(component.presenter())
    }
  }

  private class TestInteractorA : Interactor<TestPresenter, Router<TestInteractorA>>()
  private class TestInteractorB : Interactor<TestPresenter, Router<TestInteractorB>>()
  private class TestRouterB : Router<TestInteractorB> {
    constructor(
      interactor: TestInteractorB,
      component: InteractorComponent<TestPresenter, TestInteractorB>,
    ) : super(component, interactor, getInstance(), Thread.currentThread()) {
      interactor.setPresenter(component.presenter())
    }

    constructor(
      component: InteractorComponent<TestPresenter, TestInteractorB>,
      interactor: TestInteractorB,
      ribRefWatcher: RibRefWatcher,
    ) : super(component, interactor, ribRefWatcher, Thread.currentThread()) {
      interactor.setPresenter(component.presenter())
    }
  }

  private class TestChildInteractor : Interactor<TestPresenter, Router<TestChildInteractor>>()
  private class TestChildRouter(
    interactor: TestChildInteractor,
    component: InteractorComponent<TestPresenter, TestChildInteractor>,
  ) : Router<TestChildInteractor>(component, interactor, getInstance(), Thread.currentThread()) {
    init {
      interactor.setPresenter(component.presenter())
    }
  }

  companion object {
    private const val TEST_KEY = "test_key"
    private const val TEST_VALUE = "test_value"
  }
}
