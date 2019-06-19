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

import androidx.annotation.Nullable;

/** Factory for the creation of {@link RouterNavigator}s. */
public final class RouterNavigatorFactory {
  @Nullable private final Strategy creationStrategy;

  /**
   * Sets up the {@link RouterNavigatorFactory} to generate {@link RouterNavigator}s using the
   * provided {@link Strategy}. This allows implementers to include migration strategies, feature
   * flagging, and other tactics to select a different implementation of the {@link
   * RouterNavigator}.
   *
   * @param strategy {@link Strategy}
   */
  public RouterNavigatorFactory(final Strategy strategy) {
    creationStrategy = strategy;
  }

  /**
   * Generate a new {@link RouterNavigator}.
   *
   * @param hostRouter Hosting {@link Router}
   * @param <StateT> {@link StateT} type for the {@link RouterNavigator}
   * @return A new {@link RouterNavigator}
   */
  public <StateT extends RouterNavigatorState> RouterNavigator<StateT> create(
      final Router<?, ?> hostRouter) {
    if (creationStrategy != null) {
      return creationStrategy.create(hostRouter);
    } else {
      return new ModernRouterNavigator<>(hostRouter);
    }
  }

  /** Strategy to employ when using this factory to generate new {@link RouterNavigator}s. */
  public interface Strategy {
    /**
     * Generate a new {@link RouterNavigator}.
     *
     * @param hostRouter Hosting {@link Router}
     * @param <StateT> {@link StateT} type for the {@link RouterNavigator}
     * @return A new {@link RouterNavigator}
     */
    <StateT extends RouterNavigatorState> RouterNavigator<StateT> create(
        final Router<?, ?> hostRouter);
  }
}
