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

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/** The processor pipeline that processes one specific annotation. */
public abstract class ProcessorPipeline(protected var processContext: ProcessContext) {
  protected var errorReporter: ErrorReporter? = processContext.errorReporter
  protected var elementUtils: Elements? = processContext.elementUtils
  protected var typesUtils: Types? = processContext.typesUtils

  /** Process the annotation that is the same as processor */
  @Throws(Throwable::class)
  public fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
    processAnnotations(annotations, roundEnv)
    return false
  }

  /** @return the annotation that this Processor pipeline works on. */
  public abstract val annotationType: Class<out Annotation>

  /** Processes the annotation. */
  @Throws(Throwable::class)
  protected abstract fun processAnnotations(
    annotations: Set<TypeElement>,
    roundEnv: RoundEnvironment,
  )
}
