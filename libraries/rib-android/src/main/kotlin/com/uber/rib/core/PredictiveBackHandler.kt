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

import androidx.activity.BackEventCompat

/**
 * Optional interface for [ViewRouter]s that want to drive a custom predictive back animation.
 *
 * [RibActivity] checks whether the root router implements this interface and, when it does,
 * forwards the three gesture-progress callbacks so the router can animate the transition as the
 * user swipes. [handleBackPress] is still the commit point and remains on [Router].
 *
 * All methods have empty defaults so implementors only override what they need.
 *
 * These callbacks are only invoked by the system on devices that support predictive back (Android
 * 14+ with the gesture enabled). On older devices or when the gesture is not active they are never
 * called, so implementing this interface has no effect on legacy back behaviour.
 */
public interface PredictiveBackHandler {

  /** Called when the predictive back gesture is first detected. */
  public fun onBackStarted(backEvent: BackEventCompat) {}

  /** Called continuously as the user's finger moves during the back swipe. */
  public fun onBackProgressed(backEvent: BackEventCompat) {}

  /** Called when the user cancels the gesture (lifts finger without committing). */
  public fun onBackCancelled() {}
}
