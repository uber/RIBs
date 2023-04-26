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
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/** Base ProcessorPipeline that parses the annotated elements as type element. */
public abstract class TypeProcessorPipeline(
  processContext: ProcessContext,
) : ProcessorPipeline(processContext) {
  /**
   * Process the annotations.
   *
   * @param annotations
   * @param roundEnv
   * @throws Throwable
   */
  @Throws(Throwable::class)
  override fun processAnnotations(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    val annotatedElements: Collection<Element> = roundEnv.getElementsAnnotatedWith(annotationType)
    val annotatedTypes: MutableList<TypeElement> = ArrayList(annotatedElements.size)
    for (e in annotatedElements) {
      annotatedTypes.add(e as TypeElement)
    }
    processTypeElements(annotatedTypes)
  }

  /**
   * Process the list of [TypeElement].
   *
   * @param annotatedClasses the type element list.
   * @throws Throwable
   */
  @Throws(Throwable::class)
  protected abstract fun processTypeElements(annotatedClasses: List<TypeElement>)
}
