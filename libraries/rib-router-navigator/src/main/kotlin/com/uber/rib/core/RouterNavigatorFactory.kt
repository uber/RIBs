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

/**
 * Factory for the creation of [RouterNavigator]s.
 *
 * Sets up the [RouterNavigatorFactory] to generate [RouterNavigator]s using the provided
 * [Strategy]. This allows implementers to include migration strategies, feature flagging, and other
 * tactics to select a different implementation of the [RouterNavigator].
 *
 * @param creationStrategy [Strategy] `null` for the default strategy.
 */
public class RouterNavigatorFactory(private val creationStrategy: Strategy?) {
  /**
   * Generate a new [RouterNavigator].
   *
   * @param hostRouter Hosting [Router]
   * @param <StateT> [StateT] type for the [RouterNavigator]
   * @return A new [RouterNavigator]
   */
  public open fun <StateT : RouterNavigatorState> create(
    hostRouter: Router<*>,
  ): RouterNavigator<StateT> {
    return creationStrategy?.create(hostRouter) ?: StackRouterNavigator(hostRouter)
  }

  /** Strategy to employ when using this factory to generate new [RouterNavigator]s. */
  public interface Strategy {
    /**
     * Generate a new [RouterNavigator].
     *
     * @param hostRouter Hosting [Router]
     * @param <StateT> [StateT] type for the [RouterNavigator]
     * @return A new [RouterNavigator]
     */
    public fun <StateT : RouterNavigatorState> create(
      hostRouter: Router<*>,
    ): RouterNavigator<StateT>
  }
}
