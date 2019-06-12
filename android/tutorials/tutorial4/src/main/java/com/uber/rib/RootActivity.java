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

package com.uber.rib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.uber.autodispose.AutoDispose;
import com.uber.rave.Rave;
import com.uber.rave.RaveException;
import com.uber.rib.core.Optional;
import com.uber.rib.core.RibActivity;
import com.uber.rib.core.ViewRouter;
import com.uber.rib.root.RootBuilder;
import com.uber.rib.root.RootInteractor;
import com.uber.rib.root.RootRouter;
import com.uber.rib.root.RootWorkflow;
import com.uber.rib.root.RootWorkflowModel;
import com.uber.rib.root.WorkflowFactory;

import javax.annotation.Nullable;

import io.reactivex.functions.Consumer;

/** The sample app's single activity. */
public class RootActivity extends RibActivity {

  private RootInteractor rootInteractor;

  @SuppressWarnings("unchecked")
  @Override
  protected ViewRouter<?, ?, ?> createRouter(ViewGroup parentViewGroup) {
    RootBuilder rootBuilder = new RootBuilder(new RootBuilder.ParentComponent() {});
    RootRouter router = rootBuilder.build(parentViewGroup);
    rootInteractor = router.getInteractor();
    return router;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getIntent() != null) {
      handleDeepLink(getIntent());
    }
  }

  private void handleDeepLink(Intent intent) {
    RootWorkflow<RootReturnValue, ?> rootWorkflow = new WorkflowFactory().getWorkflow(intent);
    if (rootWorkflow != null) {
      try {
        Rave.getInstance().validate(rootWorkflow.getDeepLinkModel());

        rootWorkflow
            .createSingle(rootInteractor)
            .as(AutoDispose.<Optional<RootReturnValue>>autoDisposable(this))
            .subscribe(
                new Consumer<Optional<?>>() {
                  @Override
                  public void accept(Optional<?> optional) throws Exception {}
                });
      } catch (RaveException exception) {
        Log.e("RootActivity", "Invalid deep link model received.", exception);
      }
    }
  }

  private class RootReturnValue {

  }
}
