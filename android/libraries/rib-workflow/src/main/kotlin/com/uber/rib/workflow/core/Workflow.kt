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
package com.uber.rib.workflow.core

import com.google.common.base.Optional
import io.reactivex.Single

/**
 * Encapsulates a series of [Step] instances to be performed in a sequential sequence.
 *
 * @param <TReturnValue> expected return value for the entire workflow.
 * @param <TRootActionableItem> initial [ActionableItem] type for this workflow.
 *   </TRootActionableItem></TReturnValue>
 */
abstract class Workflow<TReturnValue, TRootActionableItem : ActionableItem> {
  /**
   * Creates a single to execute a workflow.
   *
   * @param rootActionableItem actionable item to start the workflow with.
   * @return an Rx [Single] that will return the workflow when subscribed to.
   */
  @SuppressWarnings("RxJavaToSingle") // Replace singleOrError() with firstOrError()
  open fun createSingle(rootActionableItem: TRootActionableItem): Single<Optional<TReturnValue>> {
    return getSteps(rootActionableItem).asResultObservable().singleOrError()
  }

  /**
   * @param rootActionableItem to create steps from.
   * @return steps to be performed for this workflow.
   */
  protected abstract fun getSteps(
    rootActionableItem: TRootActionableItem,
  ): Step<TReturnValue, out ActionableItem>
}
