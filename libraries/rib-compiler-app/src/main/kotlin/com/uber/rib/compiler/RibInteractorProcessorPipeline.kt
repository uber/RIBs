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

import com.uber.rib.core.RibInteractor
import java.io.IOException
import java.util.ArrayList
import javax.lang.model.element.TypeElement

/** The processor pipeline for [RibInteractor] */
public open class RibInteractorProcessorPipeline(
  processContext: ProcessContext,
  private var interactorGenerator: Generator<InteractorAnnotatedClass>?,
) : TypeProcessorPipeline(processContext) {
  private val annotationVerifier: AnnotationVerifier<InteractorAnnotatedClass>
  private val builderAnnotatedClassesList: MutableList<InteractorAnnotatedClass?> = ArrayList()

  /** @return the annotation type this processor pipeline deals with. */
  override val annotationType: Class<out Annotation>
    get() = SUPPORT_ANNOTATION_TYPE

  /**
   * Process the type elements.
   *
   * @param annotatedTypes annotation types.
   * @throws Throwable exception during annotation process.
   */
  @Throws(Throwable::class)
  override fun processTypeElements(annotatedTypes: List<TypeElement>) {
    parseTypeElements(annotatedTypes)
    generateSource()
  }

  /** Generate the source code. */
  @Throws(IOException::class)
  protected fun generateSource() {
    if (interactorGenerator == null) {
      return
    }
    for (interactorAnnotatedClass in builderAnnotatedClassesList) {
      interactorGenerator?.generate(interactorAnnotatedClass!!)
    }
  }

  /**
   * Verifies the element and get [InteractorAnnotatedClass] info.
   *
   * @param annotatedTypes the annotated types.
   */
  @Throws(Throwable::class)
  protected fun parseTypeElements(annotatedTypes: List<TypeElement>) {
    for (typeElement in annotatedTypes) {
      val builderAnnotatedClass = annotationVerifier.verify(typeElement)
      builderAnnotatedClassesList.add(builderAnnotatedClass)
    }
  }

  public companion object {
    @JvmField public val SUPPORT_ANNOTATION_TYPE: Class<RibInteractor> = RibInteractor::class.java
  }

  /**
   * Constructor.
   *
   * @param processContext the [ProcessContext].
   * @param interactorGenerator the code generator.
   */
  init {
    annotationVerifier = InteractorAnnotationVerifier(errorReporter!!, elementUtils!!, typesUtils!!)
    this.interactorGenerator = interactorGenerator
  }
}
