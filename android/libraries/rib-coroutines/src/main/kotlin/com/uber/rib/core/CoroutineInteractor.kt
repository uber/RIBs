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

import kotlinx.coroutines.*

/**
 * [Interactor] Interactor that launches CoroutineScope on main by default to allow simpler access of suspending functions.
 *
 * @param <P> the type of [Presenter].
 * @param <R> the type of [Router].
 */
abstract class CoroutineInteractor<P : Any, R : Router<*>> protected constructor(
  @JvmField protected override var presenter: P
) : BasicInteractor<P, R>(presenter) {

    /**
     * Called when attached in main CoroutineScope. The presenter will automatically be added when this happens.
     *
     * @param savedInstanceState the saved [Bundle].
     * @param mainScope the [CoroutineScope] that launched in [RibDispatchers.Main].
     */
    abstract suspend fun didBecomeActive(savedInstanceState: Bundle?, mainScope : CoroutineScope)

    final override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        mainScope.launch {
            didBecomeActive(savedInstanceState, this)
        }
    }
}
