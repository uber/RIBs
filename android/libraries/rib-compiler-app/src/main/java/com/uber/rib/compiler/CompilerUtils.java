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
package com.uber.rib.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

/** Handy functions for generating code. */
public class CompilerUtils {

  private CompilerUtils() {}

  /**
   * Returns the name of the package that the given type is in. If the type is in the default
   * (unnamed) package then the name is the empty string.
   */
  static String packageNameOf(Element type) {
    while (true) {
      Element enclosing = type.getEnclosingElement();
      if (enclosing == null) {
        throw new RuntimeException("null value from type.getEnclosingElement()");
      }
      if (enclosing instanceof PackageElement) {
        return ((PackageElement) enclosing).getQualifiedName().toString();
      }
      type = enclosing;
    }
  }
}
