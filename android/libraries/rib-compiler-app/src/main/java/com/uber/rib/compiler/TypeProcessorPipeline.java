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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/** Base ProcessorPipeline that parses the annotated elements as type element. */
public abstract class TypeProcessorPipeline extends ProcessorPipeline {

  /**
   * Constructor.
   *
   * @param processContext the {@link ProcessContext}.
   */
  public TypeProcessorPipeline(ProcessContext processContext) {
    super(processContext);
  }

  /**
   * Process the annotations.
   *
   * @param annotations
   * @param roundEnv
   * @throws Throwable
   */
  @Override
  protected final void processAnnotations(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Throwable {
    Collection<? extends Element> annotatedElements =
        roundEnv.getElementsAnnotatedWith(getAnnotationType());
    List<TypeElement> annotatedTypes = new ArrayList<>(annotatedElements.size());
    for (Element e : annotatedElements) {
      annotatedTypes.add((TypeElement) e);
    }

    processTypeElements(annotatedTypes);
  }

  /**
   * Process the list of {@link TypeElement}.
   *
   * @param annotatedClasses the type element list.
   * @throws Throwable
   */
  protected abstract void processTypeElements(List<TypeElement> annotatedClasses) throws Throwable;
}
