/*
 * Copyright (C) 2023. Uber Technologies
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
import io.reactivex.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LazyBackingPropertyTest {
  @Volatile private var _expensiveObject: ExpensiveObject? = null
  private val expensiveObject
    get() = ::_expensiveObject.setIfNullAndGet { ExpensiveObject() }

  @Test
  fun `Stress test fetching a LazyBackingProperty from multiple concurrent coroutines`() = runTest {
    val set =
      produce(RibDispatchers.IO) { repeat(10_000) { launch { send(expensiveObject) } } }
        .toList()
        .toSet()
    assertThat(set).hasSize(1)
  }

  @Test
  fun `Value is preserved on mocked class`() {
    val instance = mock<ClassToBeMocked>()
    instance.prop.test().assertValues(1, 2, 3)
  }
}

private open class ClassToBeMocked {
  @Volatile private var _prop: Observable<Int>? = null
  val prop: Observable<Int>
    get() = ::_prop.setIfNullAndGet { Observable.just(1, 2, 3) }
}

private class ExpensiveObject {
  init {
    Thread.sleep(100) // Simulate expensive object to make concurrent access more frequent
  }
}
