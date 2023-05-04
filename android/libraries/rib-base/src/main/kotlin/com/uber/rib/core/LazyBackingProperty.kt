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

import kotlin.reflect.KMutableProperty0

/**
 * Lazily sets a value produced by [initializer] into the receiver mutable property and returns it
 * if the property is set to `null`, or returns the value set into the property without calling
 * [initializer].
 *
 * This is similar to [lazy].
 *
 * This function is needed because of Mockito mocking. When we mock a class, mockito does not call
 * any constructor and does not initialize private fields of the class. By having a null (unset)
 * field being dynamically set by a final function, we can overcome this issue.
 *
 * To properly support concurrency, the backing mutable property should be [Volatile].
 */
internal inline fun <T : Any> KMutableProperty0<T?>.setIfNullAndGet(
  initializer: () -> T,
): T = get() ?: synchronized(Lock) { get() ?: initializer().also { set(it) } }

private object Lock
