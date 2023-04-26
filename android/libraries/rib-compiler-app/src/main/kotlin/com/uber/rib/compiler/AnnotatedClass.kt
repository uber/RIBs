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

import javax.lang.model.element.TypeElement

/** Information to a class. */
public abstract class AnnotatedClass(
  /** @return the type element that this wraps. */
  public open val typeElement: TypeElement,
) {

  /**
   * Get the root name of the element. For instance, if the annotated class was "FooInteractor",
   * this would return "Foo".
   *
   * @return the root name.
   */
  public open val rootName: String by lazy {
    typeElement.simpleName.toString().substringBefore(nameSuffix)
  }

  /** Set if code has been generated. */
  public open var isCodeGenerated: Boolean = false

  /** @return the annotated class name suffix */
  public abstract val nameSuffix: String
}
