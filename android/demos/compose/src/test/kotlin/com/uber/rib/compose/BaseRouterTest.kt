package com.uber.rib.compose

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.RouterHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class BaseRouterTest : RibTestBasePlaceholder() {

  @Mock internal lateinit var component: BaseBuilder.Component
  @Mock internal lateinit var interactor: BaseInteractor
  @Mock internal lateinit var view: BaseView

  private var router: BaseRouter? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)

    router = BaseRouter(view, interactor, component)
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

