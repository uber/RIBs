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
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Verifier that checks if the type element is valid and returns information as [AnnotatedClass].
 *
 * @param <T> the actual [AnnotatedClass] info that it generates if valid.
 */
internal abstract class AnnotationVerifier<T : AnnotatedClass>(
  protected val errorReporter: ErrorReporter,
  protected val elementUtils: Elements,
  protected val typesUtils: Types,
) {

  /**
   * Verify that a given type has applied annotations correctly.
   *
   * @param type the type to check.
   * @return an [AnnotatedClass] object for the type.
   * @throws VerificationFailedException when verification fails.
   */
  @Throws(VerificationFailedException::class) abstract fun verify(type: TypeElement): T
}
