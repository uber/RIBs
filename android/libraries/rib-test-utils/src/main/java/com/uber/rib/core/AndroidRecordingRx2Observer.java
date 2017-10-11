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

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * AndroidRecordingObserver implementation from RxBinding.
 *
 * @param <T> Parametrized type.
 */
public final class AndroidRecordingRx2Observer<T> implements Observer<T> {

  private final BlockingDeque<Object> events = new LinkedBlockingDeque<>();

  @Override
  public void onSubscribe(Disposable disposable) {}

  @Override
  public void onComplete() {
    events.addLast(new OnCompleted());
  }

  @Override
  public void onError(Throwable e) {
    events.addLast(new OnError(e));
  }

  @Override
  public void onNext(T t) {
    events.addLast(new OnNext(t));
  }

  T takeNext() {
    OnNext event = takeEvent(OnNext.class);
    return event.value;
  }

  Throwable takeError() {
    OnError event = takeEvent(OnError.class);
    return event.throwable;
  }

  private <E> E takeEvent(Class<E> wanted) {
    Object event;
    try {
      event = events.pollFirst(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (event == null) {
      throw new NoSuchElementException(
          "No event found while waiting for " + wanted.getSimpleName());
    }
    assertThat(event).isInstanceOf(wanted);
    return wanted.cast(event);
  }

  void assertNoMoreEvents() {
    try {
      Object event = takeEvent(Object.class);
      throw new IllegalStateException("Expected no more events but got " + event);
    } catch (NoSuchElementException ignored) {
      // Can be ignored in this case
    }
  }

  private final class OnNext {

    final T value;

    private OnNext(T value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "OnNext[" + value + "]";
    }
  }

  private static final class OnCompleted {

    @Override
    public String toString() {
      return "OnCompleted";
    }
  }

  private static final class OnError {

    private final Throwable throwable;

    private OnError(Throwable throwable) {
      this.throwable = throwable;
    }

    @Override
    public String toString() {
      return "OnError[" + throwable + "]";
    }
  }
}
