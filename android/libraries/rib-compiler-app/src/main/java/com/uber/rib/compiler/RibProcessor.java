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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/** Process the annotations with {@link ProcessorPipeline}. */
public abstract class RibProcessor extends AbstractProcessor implements ProcessContext {

  protected ErrorReporter errorReporter;
  protected Elements elementUtils;
  protected Types typesUtils;

  List<ProcessorPipeline> processorPipelines = new ArrayList<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    errorReporter = new ErrorReporter(processingEnv.getMessager());
    typesUtils = processingEnv.getTypeUtils();
    processorPipelines.addAll(getProcessorPipelines(this));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    if (roundEnv.processingOver()) {
      return false;
    }

    for (ProcessorPipeline processorPipeline : processorPipelines) {
      try {
        processorPipeline.process(annotations, roundEnv);
      } catch (Throwable e) {
        errorReporter.reportError(
            String.format(
                Locale.getDefault(),
                "Fatal error running %s processor: %s",
                processorPipeline.getAnnotationType().getSimpleName(),
                e.getMessage()));
      }
    }
    return false;
  }

  /**
   * Get list of {@link ProcessorPipeline} to process each annotation.
   *
   * @param processContext the {@link ProcessContext}.
   * @return the list of processor pipelines.
   */
  protected abstract List<ProcessorPipeline> getProcessorPipelines(ProcessContext processContext);

  @Override
  public ErrorReporter getErrorReporter() {
    return errorReporter;
  }

  @Override
  public Elements getElementUtils() {
    return elementUtils;
  }

  @Override
  public Types getTypesUtils() {
    return typesUtils;
  }
}
