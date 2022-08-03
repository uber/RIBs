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
import com.uber.rib.core.XRay.Companion.apply
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class XRayTest {

  @Test
  fun apply_changesViewBackground() {
    XRay.toggle()
    val viewRouter: ViewRouter<*, *> = mock()
    val view: View = mock {
      on { context } doReturn(RuntimeEnvironment.application.baseContext)
    }
    apply(viewRouter, view)
    verify(view).background = any()
    verifyNoMoreInteractions(viewRouter)
  }
}
