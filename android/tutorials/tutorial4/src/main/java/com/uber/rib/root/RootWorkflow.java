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
import com.uber.rib.workflow.core.Step;
import com.uber.rib.workflow.core.Workflow;
import java.io.Serializable;

/**
 * Represents a workflow that is launched starting at {@link RootActionableItem}.
 *
 * @param <TWorkflowReturnType> the type of value returned by the workflow (if any).
 * @param <TDeepLinkModel> the type of deep link model used by this workflow.
 */
public abstract class RootWorkflow<TWorkflowReturnType, TDeepLinkModel extends RootWorkflowModel>
    extends Workflow<TWorkflowReturnType, RootActionableItem> implements Serializable {

  private final TDeepLinkModel deepLinkModel;

  /**
   * Constructor to create a {@link RootWorkflow} from an {@link Intent}.
   *
   * @param deepLinkIntent the raw deep link {@link Intent} to parse for the workflow.
   */
  public RootWorkflow(Intent deepLinkIntent) {
    this.deepLinkModel = parseDeepLinkIntent(deepLinkIntent);
  }

  /**
   * Constructor to create a {@link RootWorkflow} from a deep link model.
   *
   * @param deepLinkModel
   */
  public RootWorkflow(TDeepLinkModel deepLinkModel) {
    this.deepLinkModel = deepLinkModel;
  }

  @Override
  protected final Step<TWorkflowReturnType, ?> getSteps(RootActionableItem rootActionableItem) {
    return getSteps(rootActionableItem, deepLinkModel);
  }

  /**
   * @return the model for the workflow.
   */
  public TDeepLinkModel getDeepLinkModel() {
    return deepLinkModel;
  }

  /**
   * @param rootActionableItem to create steps with.
   * @param deepLinkModel to create deep link data from.
   * @return steps to be performed for this workflow.
   */
  protected abstract Step<TWorkflowReturnType, ?> getSteps(
      RootActionableItem rootActionableItem, TDeepLinkModel deepLinkModel);

  /**
   * Responsible for turning a raw URI into a deep link model with a rigid schema.
   *
   * @param deepLinkIntent the raw deep link {@link Intent}.
   * @return the deep link model.
   */
  protected abstract TDeepLinkModel parseDeepLinkIntent(Intent deepLinkIntent);
}
