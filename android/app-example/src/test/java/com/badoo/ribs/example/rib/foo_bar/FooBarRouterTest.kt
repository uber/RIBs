package com.badoo.ribs.example.rib.foo_bar

import com.badoo.common.rib.ViewFactory

import com.nhaarman.mockitokotlin2.mock
import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.NodeHelper

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class FooBarRouterTest : RibTestBasePlaceholder() {

  private var interactor: FooBarInteractor = mock()
  private var viewFactory: ViewFactory<FooBarView> = mock()
  private var router: FooBarNode? = null

  @Before
  fun setup() {
    router = FooBarNode(viewFactory, interactor)
    NodeHelper.attach(router)
  }

  @After
  fun tearDown() {
      NodeHelper.detach(router)
  }

  /**
   * TODO: Add real tests.
   */
  @Test
  fun `an example test with some conditions should pass`() {
    throw RuntimeException("Add real tests.")
  }
}
