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
package com.uber.rib.root;

import android.content.Intent;
import androidx.annotation.Nullable;

public class WorkflowFactory {
  @Nullable
  public RootWorkflow getWorkflow(Intent intent) {
    // If this was a real app you would likely write a pattern for each workflow object to
    // independently declare which intent it applied to. Then you would pick the first match.
    // Instead lets just do some simple if-else branches here.
    if (intent != null && intent.getData() != null) {
      // TODO: return a workflow here
      return null;
    }
    return null;
  }
}
