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

import static com.uber.rib.core.Preconditions.checkNotNull;

/**
 * An immutable object that may contain a non-null reference to another object. Each instance of
 * this type either contains a non-null reference, or contains nothing (in which case we say that
 * the reference is "absent"); it is never said to "contain {@code null}".
 *
 * <p>A non-null {@code Optional<T>} reference can be used as a replacement for a nullable {@code T}
 * reference. It allows you to represent "a {@code T} that must be present" and a "a {@code T} that
 * might be absent" as two distinct types in your program, which can aid clarity.
 *
 * @param <T> the type of instance that can be contained. {@code Optional} is naturally covariant on
 *     this type, so it is safe to cast an {@code Optional<T>} to {@code Optional<S>} for any
 *     supertype {@code S} of {@code T}.
 */
public abstract class Optional<T> {

  Optional() {}

  /** Returns an {@code Optional} instance with no contained reference. */
  public static <T> Optional<T> absent() {
    return Absent.withType();
  }

  /** Returns an {@code Optional} instance containing the given non-null reference. */
  public static <T> Optional<T> of(T reference) {
    return new Present<T>(checkNotNull(reference));
  }

  /**
   * If {@code nullableReference} is non-null, returns an {@code Optional} instance containing that
   * reference; otherwise returns {@link Optional#absent}.
   */
  public static <T> Optional<T> fromNullable(@Nullable T nullableReference) {
    return (nullableReference == null) ? Optional.<T>absent() : new Present<T>(nullableReference);
  }

  /** Returns {@code true} if this holder contains a (non-null) instance. */
  public abstract boolean isPresent();

  /**
   * Returns the contained instance, which must be present. If the instance might be absent, use
   * {@link #or(Object)} or {@link #orNull} instead.
   *
   * @throws IllegalStateException if the instance is absent ({@link #isPresent} returns {@code
   *     false})
   */
  public abstract T get();

  /** Returns the contained instance if it is present; {@code defaultValue} otherwise. */
  public abstract T or(T defaultValue);

  /** Returns this {@code Optional} if it has a value present; {@code secondChoice} otherwise. */
  public abstract Optional<T> or(Optional<? extends T> secondChoice);

  /** Returns the contained instance if it is present; {@code null} otherwise. */
  @Nullable
  public abstract T orNull();

  /**
   * If the instance is present, it is transformed with the given {@link Function}; otherwise,
   * {@link Optional#absent} is returned.
   *
   * <p><b>Comparison to {@code java.util.Optional}:</b> this method is similar to Java 8's {@code
   * Optional.map}, except when {@code function} returns {@code null}. In this case this method
   * throws an exception, whereas the Java 8 method returns {@code Optional.absent()}.
   *
   * @param function The function to transform the optional
   * @param <V> The parameter type
   * @return The transformed optional
   * @throws NullPointerException if the function returns {@code null}
   * @since 12.0
   */
  public abstract <V> Optional<V> transform(Function<? super T, V> function);

  /**
   * Returns {@code true} if {@code object} is an {@code Optional} instance, and either the
   * contained references are {@linkplain Object#equals equal} to each other or both are absent.
   * Note that {@code Optional} instances of differing parameterized types can be equal.
   */
  @Override
  public abstract boolean equals(Object object);

  /** Returns a hash code for this instance. */
  @Override
  public abstract int hashCode();

  /**
   * Returns a string representation for this instance. The form of this string representation is
   * unspecified.
   */
  @Override
  public abstract String toString();
}
