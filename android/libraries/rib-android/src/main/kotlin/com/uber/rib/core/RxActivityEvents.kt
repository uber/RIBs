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

import com.uber.rib.core.lifecycle.ActivityCallbackEvent
import com.uber.rib.core.lifecycle.ActivityLifecycleEvent
import io.reactivex.Observable

/** Interface for reactive activities. */
interface RxActivityEvents {
  /** @return an observable of this activity's lifecycle events. */
  fun lifecycle(): Observable<ActivityLifecycleEvent>

  /** @return an observable of this activity's lifecycle events. */
  fun callbacks(): Observable<ActivityCallbackEvent>

  /**
   * @param <T> The type of [ActivityLifecycleEvent] subclass you want.
   * @param clazz The [ActivityLifecycleEvent] subclass you want.
   * @return an observable of this activity's lifecycle events.
   */
  @JvmDefault
  fun <T : ActivityLifecycleEvent> lifecycle(clazz: Class<T>): Observable<T> {
    return lifecycle()
      .filter { activityEvent -> clazz.isAssignableFrom(activityEvent.javaClass) }
      .cast(clazz)
  }

  /**
   * @param <T> The type of [ActivityCallbackEvent] subclass you want.
   * @param clazz The [ActivityCallbackEvent] subclass you want.
   * @return an observable of this activity's callbacks events.
   */
  @JvmDefault
  fun <T : ActivityCallbackEvent> callbacks(clazz: Class<T>): Observable<T> {
    return callbacks()
      .filter { activityEvent -> clazz.isAssignableFrom(activityEvent.javaClass) }
      .cast(clazz)
  }
}
