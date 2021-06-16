package com.uber.rib.compose.root.main.logged_out

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.InteractorHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LoggedOutInteractorTest : RibTestBasePlaceholder() {

  @Mock internal lateinit var presenter: LoggedOutInteractor.LoggedOutPresenter
  @Mock internal lateinit var router: LoggedOutRouter

  private var interactor: LoggedOutInteractor? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)

    interactor = TestLoggedOutInteractor.create(presenter)
  }

  /**
   * TODO: Delete this example and add real tests.
   */
  @Test
  fun anExampleTest_withSomeConditions_shouldPass() {
    // Use InteractorHelper to drive your interactor's lifecycle.
    InteractorHelper.attach<LoggedOutInteractor.LoggedOutPresenter, LoggedOutRouter>(interactor!!, presenter, router, null)
    InteractorHelper.detach(interactor!!)

    throw RuntimeException("Remove this test and add real tests.")
  }
}