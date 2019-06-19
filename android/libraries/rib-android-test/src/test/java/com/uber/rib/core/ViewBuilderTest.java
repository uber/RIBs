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

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.uber.rib.android.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ViewBuilderTest {

  @Test
  public void createView_shouldUseInflateViewToCreateView() {
    ViewGroup parentViewGroup = new FrameLayout(RuntimeEnvironment.application);

    final Holder holder = new Holder();

    ViewBuilder viewBuilder =
        new ViewBuilder(new Object()) {
          @NonNull
          @Override
          protected View inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
            holder.inflaterContext = inflater.getContext();
            holder.inflaterViewGroup = parentViewGroup;
            return null;
          }
        };

    viewBuilder.createView(parentViewGroup);

    assertThat(holder.inflaterContext).isEqualTo(parentViewGroup.getContext());
    assertThat(holder.inflaterViewGroup).isEqualTo(parentViewGroup);
  }

  @Test
  public void createView_useCustomContext() {
    ViewGroup parentViewGroup = new FrameLayout(RuntimeEnvironment.application);
    final ContextThemeWrapper customContext =
        new ContextThemeWrapper(RuntimeEnvironment.application, R.style.Theme_AppCompat);

    final Holder holder = new Holder();

    ViewBuilder viewBuilder =
        new ViewBuilder(new Object()) {
          @NonNull
          @Override
          protected View inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
            holder.inflaterContext = inflater.getContext();
            holder.inflaterViewGroup = parentViewGroup;
            return null;
          }

          @NonNull
          @Override
          protected Context onThemeContext(@NonNull Context parentContext) {
            return customContext;
          }
        };

    viewBuilder.createView(parentViewGroup);

    assertThat(holder.inflaterContext).isEqualTo(customContext);
    assertThat(holder.inflaterViewGroup).isEqualTo(parentViewGroup);
  }

  private static class Holder {
    private Context inflaterContext;
    private ViewGroup inflaterViewGroup;
  }
}
