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

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Base class to encapsulate generating something.
 *
 * @param <T> the {@link AnnotatedClass} that the {@link Generator#generate(AnnotatedClass)} needs.
 */
public abstract class Generator<T extends AnnotatedClass> {

  protected final ProcessingEnvironment processingEnvironment;
  protected final ErrorReporter errorReporter;

  /**
   * Constructor.
   *
   * @param processingEnvironment the current {@link ProcessingEnvironment}.
   * @param errorReporter the {@link ErrorReporter} for error output
   */
  public Generator(ProcessingEnvironment processingEnvironment, ErrorReporter errorReporter) {
    this.processingEnvironment = processingEnvironment;
    this.errorReporter = errorReporter;
  }

  /** @return the current {@link ProcessingEnvironment}. */
  protected ProcessingEnvironment getProcessingEnvironment() {
    return processingEnvironment;
  }

  /**
   * Generator something for an interactor.
   *
   * @param builder metadata.
   * @throws IOException when something goes wrong.
   */
  public abstract void generate(T builder) throws IOException;
}
