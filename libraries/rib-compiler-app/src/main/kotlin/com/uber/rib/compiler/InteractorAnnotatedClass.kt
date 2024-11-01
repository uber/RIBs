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
import javax.lang.model.element.VariableElement

public open class InteractorAnnotatedClass(
  typeElement: TypeElement,
  /** @return the list of dependencies. */
  public open val dependencies: List<VariableElement>,
  /** @return Whether this interactor extends BasicInteractor. */
  public open val isBasic: Boolean,
) : AnnotatedClass(typeElement) {

  open override val nameSuffix: String
    get() = Constants.INTERACTOR_SUFFIX
}
