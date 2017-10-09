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

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * * Verifier that checks if the type element is valid and returns information as {@link
 * AnnotatedClass}.
 *
 * @param <T> the actual {@link AnnotatedClass} info that it generates if valid.
 */
public abstract class AnnotationVerifier<T extends AnnotatedClass> {

  protected final Elements elementUtils;
  protected final ErrorReporter errorReporter;
  protected final Types typesUtils;

  /**
   * Constructor.
   *
   * @param errorReporter a errorReporter.
   * @param elementUtils element utilities.
   * @param typesUtils type utilities.
   */
  public AnnotationVerifier(ErrorReporter errorReporter, Elements elementUtils, Types typesUtils) {
    this.errorReporter = errorReporter;
    this.elementUtils = elementUtils;
    this.typesUtils = typesUtils;
  }

  /**
   * Verify that a given type has applied annotations correctly.
   *
   * @param type the type to check.
   * @return an {@link AnnotatedClass} object for the type.
   * @throws VerificationFailedException when verification fails.
   */
  public abstract T verify(TypeElement type) throws VerificationFailedException;
}
