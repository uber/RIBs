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

import java.util.ArrayList
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/** Process the annotations with [ProcessorPipeline]. */
public abstract class RibProcessor : AbstractProcessor(), ProcessContext {

  override var errorReporter: ErrorReporter? = null
    protected set
  override var elementUtils: Elements? = null
    protected set
  override var typesUtils: Types? = null
    protected set

  public var processorPipelines: MutableList<ProcessorPipeline> = ArrayList()

  @Synchronized
  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    elementUtils = processingEnv.elementUtils
    errorReporter = ErrorReporter(processingEnv.messager)
    typesUtils = processingEnv.typeUtils
    processorPipelines.addAll(getProcessorPipelines(this))
  }

  override fun getSupportedSourceVersion(): SourceVersion {
    return SourceVersion.latestSupported()
  }

  override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
    if (roundEnv.processingOver()) {
      return false
    }
    for (processorPipeline in processorPipelines) {
      try {
        processorPipeline.process(annotations, roundEnv)
      } catch (e: Throwable) {
        errorReporter?.reportError(
          "Fatal error running ${processorPipeline.annotationType.simpleName} processor: ${e.message}",
        )
      }
    }
    return false
  }

  /**
   * Get list of [ProcessorPipeline] to process each annotation.
   *
   * @param processContext the [ProcessContext].
   * @return the list of processor pipelines.
   */
  protected abstract fun getProcessorPipelines(
    processContext: ProcessContext,
  ): List<ProcessorPipeline>
}
