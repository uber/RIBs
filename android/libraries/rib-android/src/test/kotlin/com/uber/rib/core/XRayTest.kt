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

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class XRayTest {
  @Before
  fun setup() {
    XRay.setup(XRayConfig(enabled = true))
  }

  @Test
  fun `test initial value`() {
    XRay.setup(XRayConfig())
    assertFalse("XRay must be disabled by default", XRay.isEnabled())
  }

  @Test
  fun `apply function changes view background`() {
    val view: View = mock { on { context } doReturn RuntimeEnvironment.application.baseContext }
    XRay.apply("Test", view)
    verify(view).background = any()
    verify(view).alpha = 0.9f
  }

  @Test
  fun `apply function changes view background but with alpha disabled`() {
    XRay.setup(XRayConfig(enabled = true, alphaEnabled = false))
    val view: View = mock { on { context } doReturn RuntimeEnvironment.application.baseContext }
    XRay.apply("Test", view)
    verify(view).background = any()
    verify(view, never()).alpha = any()
  }

  @Test
  fun `getShortRibletName must short router name`() {
    assertEquals("Test", XRay.getShortRibletName("TestRouter"))
    assertEquals("Router", XRay.getShortRibletName("Router"))
  }

  @Test
  fun `xray toggle`() {
    XRay.toggle()
    assertFalse(XRay.isEnabled())

    XRay.toggle()
    assertTrue(XRay.isEnabled())
  }
}
