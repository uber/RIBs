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
package com.uber.rib.compiler

import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement

/** Handy functions for generating code. */
public open class CompilerUtils {
  public companion object {
    /**
     * Returns the name of the package that the given type is in. If the type is in the default
     * (unnamed) package then the name is the empty string.
     */
    @JvmStatic
    public fun packageNameOf(type: Element): String {
      var type = type
      while (true) {
        val enclosing =
          type.enclosingElement
            ?: throw RuntimeException("null value from type.getEnclosingElement()")
        if (enclosing is PackageElement) {
          return enclosing.qualifiedName.toString()
        }
        type = enclosing
      }
    }
  }
}
