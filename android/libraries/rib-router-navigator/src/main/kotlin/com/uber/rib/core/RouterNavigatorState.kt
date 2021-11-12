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

/** Represents states for [StackRouterNavigator]. Most often implemented with an enum. */
interface RouterNavigatorState {

  /** @return identifier for a [StackRouterNavigator] state. */
  @JvmDefault
  fun stateName(): String {
    return if (this.javaClass.isEnum) {
      (this as Enum<*>).name
    } else {
      throw java.lang.AssertionError("Must be implemented by enum or override stateName()")
    }
  }
}
