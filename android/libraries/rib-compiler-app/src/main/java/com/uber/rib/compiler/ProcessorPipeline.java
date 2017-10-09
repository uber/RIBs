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

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/** The processor pipeline that processes one specific annotation. */
public abstract class ProcessorPipeline {

  protected ErrorReporter errorReporter;
  protected Elements elementUtils;
  protected ProcessContext processContext;
  protected Types typesUtils;

  /**
   * Constructor.
   *
   * @param processContext the {@link ProcessContext}.
   */
  public ProcessorPipeline(ProcessContext processContext) {
    this.processContext = processContext;
    this.errorReporter = processContext.getErrorReporter();
    this.elementUtils = processContext.getElementUtils();
    this.typesUtils = processContext.getTypesUtils();
  }

  /**
   * Process the annotation that is the same as processor
   *
   * @param annotations
   * @param roundEnv
   * @return
   * @throws Throwable
   */
  protected final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
      throws Throwable {
    processAnnotations(annotations, roundEnv);
    return false;
  }

  /** @return the annotation that this Processor pipeline works on. */
  public abstract Class<? extends Annotation> getAnnotationType();

  /**
   * Processes the annotation.
   *
   * @param annotations
   * @param roundEnv
   * @throws Throwable
   */
  protected abstract void processAnnotations(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Throwable;
}
