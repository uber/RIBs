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

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;

/** Process the annotations for all added annotation processor pipelines. */
@AutoService(Processor.class)
public final class RibTestProcessor extends RibProcessor {

  private InteractorTestGenerator interactorTestGenerator;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    interactorTestGenerator = new InteractorTestGenerator(processingEnv, errorReporter);
    super.init(processingEnv);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(
        RibInteractorProcessorPipeline.SUPPORT_ANNOTATION_TYPE.getCanonicalName());
  }

  @Override
  protected List<ProcessorPipeline> getProcessorPipelines(ProcessContext processContext) {
    return ImmutableList.<ProcessorPipeline>of(
        new RibInteractorProcessorPipeline(processContext, interactorTestGenerator));
  }
}
