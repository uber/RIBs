package com.uber.rib.core;

import android.support.annotation.Nullable;

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
