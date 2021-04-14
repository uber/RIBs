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
package com.uber.rib.core;

import com.uber.rib.core.lifecycle.ActivityCallbackEvent;
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

/** Interface for reactive activities. */
public interface RxActivityEvents {

  /** @return an observable of this activity's lifecycle events. */
  Observable<ActivityLifecycleEvent> lifecycle();

  /** @return an observable of this activity's lifecycle events. */
  Observable<ActivityCallbackEvent> callbacks();

  /**
   * @param <T> The type of {@link ActivityLifecycleEvent} subclass you want.
   * @param clazz The {@link ActivityLifecycleEvent} subclass you want.
   * @return an observable of this activity's lifecycle events.
   */
  default <T extends ActivityLifecycleEvent> Observable<T> lifecycle(final Class<T> clazz) {
    return lifecycle()
        // Lambdas within interfaces are not yet supported.
        .filter(
            new Predicate<ActivityLifecycleEvent>() {
              @Override
              public boolean test(final ActivityLifecycleEvent activityEvent) {
                return clazz.isAssignableFrom(activityEvent.getClass());
              }
            })
        .cast(clazz);
  }

  /**
   * @param <T> The type of {@link ActivityCallbackEvent} subclass you want.
   * @param clazz The {@link ActivityCallbackEvent} subclass you want.
   * @return an observable of this activity's callbacks events.
   */
  default <T extends ActivityCallbackEvent> Observable<T> callbacks(final Class<T> clazz) {
    return callbacks()
        // Lambdas within interfaces are not yet supported.
        .filter(
            new Predicate<ActivityCallbackEvent>() {
              @Override
              public boolean test(final ActivityCallbackEvent activityEvent) {
                return clazz.isAssignableFrom(activityEvent.getClass());
              }
            })
        .cast(clazz);
  }
}
