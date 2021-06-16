package com.uber.rib.compose

import com.uber.rib.compose.root.main.MainInteractor
import com.uber.rib.compose.root.main.MainRouter
import com.uber.rib.compose.root.RootView
import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.RouterHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RootRouterTest : RibTestBasePlaceholder() {

  @Mock internal lateinit var component: RootBuilder.Component
  @Mock internal lateinit var interactor: MainInteractor
  @Mock internal lateinit var view: RootView

  private var router: MainRouter? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)

    router = MainRouter(view, interactor, component)
  }

  /**
   * TODO: Delete this example and add real tests.
   */
  @Test
  fun anExampleTest_withSomeConditions_shouldPass() {
    // Use RouterHelper to drive your router's lifecycle.
    RouterHelper.attach(router!!)
    RouterHelper.detach(router!!)

    throw RuntimeException("Remove this test and add real tests.")
  }

}

