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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.kotlin.mock
import java.util.concurrent.atomic.AtomicBoolean

class RouterTest {

  private val component: InteractorComponent<*, *> = mock()
  private val interactor: Interactor<*, *> = mock()
  private val ribRefWatcher: RibRefWatcher = mock()

  @Test
  fun didLoad_shouldBeCalledAfterInstantiation() {
    val didLoad = AtomicBoolean(false)
    val router: Router<*> = object : Router<Interactor<*, *>>(
      component,
      interactor,
      ribRefWatcher,
      Thread.currentThread()
    ) {
      override fun attachToInteractor() {
        // ignore the Interactor since we're only testing the Router
      }

      override fun didLoad() {
        super.didLoad()
        didLoad.set(true)
      }
    }
    router.dispatchAttach(null)
    assertThat(didLoad.get()).isTrue()
  }
}
