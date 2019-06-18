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

import androidx.annotation.NonNull;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * RecordingObserver implementation from RxBinding.
 *
 * @param <T> Parametrized type.
 */
final class RecordingObserver<T> implements Observer<T> {

  @NonNull private final BlockingDeque<Object> events = new LinkedBlockingDeque<>();

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

  @NonNull
  T takeNext() {
    OnNext event = takeEvent(OnNext.class);
    return event.value;
  }

  @NonNull
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

  private final class OnNext {

    @NonNull final T value;

    private OnNext(@NonNull T value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "OnNext[" + value + "]";
    }
  }

  private final class OnCompleted {

    @Override
    public String toString() {
      return "OnCompleted";
    }
  }

  private final class OnError {

    @NonNull private final Throwable throwable;

    private OnError(@NonNull Throwable throwable) {
      this.throwable = throwable;
    }

    @Override
    public String toString() {
      return "OnError[" + throwable + "]";
    }
  }
}
