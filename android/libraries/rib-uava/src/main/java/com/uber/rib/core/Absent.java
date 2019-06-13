/*
 * Copyright (C) 2013 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


package com.uber.rib.core;

import androidx.annotation.Nullable;

import java.util.function.Function;

import static com.uber.rib.core.Preconditions.*;

/** Implementation of an {@link Optional} not containing a reference. */
final class Absent<T> extends Optional<T> {

  static final Absent<Object> INSTANCE = new Absent<Object>();

  private Absent() {}

  @SuppressWarnings("unchecked")
  static <T> Optional<T> withType() {
    return (Optional<T>) INSTANCE;
  }

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public T get() {
    throw new IllegalStateException("Optional.get() cannot be called on an absent value");
  }

  @Override
  public T or(T defaultValue) {
    return checkNotNull(defaultValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<T> or(Optional<? extends T> secondChoice) {
    return (Optional<T>) checkNotNull(secondChoice);
  }

  @Override
  @Nullable
  public T orNull() {
    return null;
  }

  @Override
  public <V> Optional<V> transform(Function<? super T, V> function) {
    checkNotNull(function);
    return Optional.absent();
  }

  @Override
  public boolean equals(Object object) {
    return object == this;
  }

  @Override
  public int hashCode() {
    return 0x598df91c;
  }

  @Override
  public String toString() {
    return "Optional.absent()";
  }
}
