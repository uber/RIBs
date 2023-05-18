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

import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor

/** Process the annotations for all added annotation processor pipelines. */
@AutoService(Processor::class)
public open class RibTestProcessor : RibProcessor() {
  private var interactorTestGenerator: InteractorTestGenerator? = null

  @Synchronized
  override fun init(processingEnv: ProcessingEnvironment) {
    interactorTestGenerator =
      InteractorTestGenerator(processingEnv, ErrorReporter(processingEnv.messager))
    super.init(processingEnv)
  }

  override fun getSupportedAnnotationTypes(): Set<String> {
    return ImmutableSet.of(RibInteractorProcessorPipeline.SUPPORT_ANNOTATION_TYPE.canonicalName)
  }

  override fun getProcessorPipelines(processContext: ProcessContext): List<ProcessorPipeline> {
    return ImmutableList.of<ProcessorPipeline>(
      RibInteractorProcessorPipeline(processContext, interactorTestGenerator),
    )
  }
}
