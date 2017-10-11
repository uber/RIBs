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

package com.uber.rib.workflow.core;

import com.uber.rib.core.Optional;

import io.reactivex.Single;

/**
 * Encapsulates a series of {@link Step} instances to be performed in a sequential sequence.
 *
 * @param <TReturnValue> expected return value for the entire workflow.
 * @param <TRootActionableItem> initial {@link ActionableItem} type for this workflow.
 */
public abstract class Workflow<TReturnValue, TRootActionableItem extends ActionableItem> {

  /**
   * Creates a single to execute a workflow.
   *
   * @param rootActionableItem actionable item to start the workflow with.
   * @return an Rx {@link Single} that will return the workflow when subscribed to.
   */
  public io.reactivex.Single<Optional<TReturnValue>> createSingle(
      TRootActionableItem rootActionableItem) {
    return getSteps(rootActionableItem).asResultObservable().singleOrError();
  }

  /**
   * @param rootActionableItem to create steps from.
   * @return steps to be performed for this workflow.
   */
  protected abstract Step<TReturnValue, ? extends ActionableItem> getSteps(
      TRootActionableItem rootActionableItem);
}
