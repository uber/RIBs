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
package com.uber.rib;

import android.view.ViewGroup;
import com.uber.rib.core.RibActivity;
import com.uber.rib.core.ViewRouter;
import com.uber.rib.root.RootBuilder;

/** The sample app's single activity. */
public class RootActivity extends RibActivity {

  @SuppressWarnings("unchecked")
  @Override
  protected ViewRouter<?, ?> createRouter(ViewGroup parentViewGroup) {
    RootBuilder rootBuilder = new RootBuilder(new RootBuilder.ParentComponent() {});
    return rootBuilder.build(parentViewGroup);
  }
}
