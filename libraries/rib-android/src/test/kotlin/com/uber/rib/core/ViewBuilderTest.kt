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

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.common.truth.Truth
import com.uber.rib.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewBuilderTest {
  @Test
  fun createView_shouldUseInflateViewToCreateView() {
    val parentViewGroup: ViewGroup = FrameLayout(RuntimeEnvironment.application)
    val holder = Holder()
    val viewBuilder: ViewBuilder<*, *, *> =
      object : ViewBuilder<View, Router<*>, Any>(Any()) {
        override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): View {
          holder.inflaterContext = inflater.context
          holder.inflaterViewGroup = parentViewGroup
          return mock()
        }
      }
    viewBuilder.createView(parentViewGroup)
    Truth.assertThat(holder.inflaterContext).isEqualTo(parentViewGroup.context)
    Truth.assertThat(holder.inflaterViewGroup).isEqualTo(parentViewGroup)
  }

  @Test
  fun createView_useCustomContext() {
    val parentViewGroup: ViewGroup = FrameLayout(RuntimeEnvironment.application)
    val customContext = ContextThemeWrapper(RuntimeEnvironment.application, R.style.Theme_AppCompat)
    val holder = Holder()
    val viewBuilder: ViewBuilder<*, *, *> =
      object : ViewBuilder<View, Router<*>, Any>(Any()) {
        override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): View {
          holder.inflaterContext = inflater.context
          holder.inflaterViewGroup = parentViewGroup
          return mock()
        }

        override fun onThemeContext(parentContext: Context): Context {
          return customContext
        }
      }
    viewBuilder.createView(parentViewGroup)
    Truth.assertThat(holder.inflaterContext).isEqualTo(customContext)
    Truth.assertThat(holder.inflaterViewGroup).isEqualTo(parentViewGroup)
  }

  private class Holder {
    var inflaterContext: Context? = null
    var inflaterViewGroup: ViewGroup? = null
  }
}
