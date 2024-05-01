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

import com.google.common.truth.Truth
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.NoSuchElementException
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

/**
 * RecordingObserver implementation from RxBinding.
 *
 * @param <T> Parametrized type.
 */
internal class RecordingObserver<T : Any> : Observer<T> {
  private val events: BlockingDeque<Any> = LinkedBlockingDeque()

  override fun onSubscribe(disposable: Disposable) {}

  override fun onComplete() {
    events.addLast(OnCompleted())
  }

  override fun onError(e: Throwable) {
    events.addLast(OnError(e))
  }

  override fun onNext(t: T) {
    events.addLast(OnNext(t))
  }

  fun takeNext(): T {
    val event: OnNext = takeEvent(OnNext::class.java)
    return event.value as T
  }

  private fun <E> takeEvent(wanted: Class<E>): E {
    val event: Any? =
      try {
        events.pollFirst(1, TimeUnit.SECONDS)
      } catch (e: InterruptedException) {
        throw RuntimeException(e)
      }
    if (event == null) {
      throw NoSuchElementException("No event found while waiting for " + wanted.simpleName)
    }
    Truth.assertThat(event).isInstanceOf(wanted)
    return event as E
  }

  private class OnNext(val value: Any) {
    override fun toString() = "OnNext[$value]"
  }

  private class OnCompleted {
    override fun toString() = "OnCompleted"
  }

  private class OnError(private val throwable: Throwable) {
    override fun toString() = "OnError[$throwable]"
  }
}
