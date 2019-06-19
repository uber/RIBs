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

import androidx.annotation.Nullable;

import com.uber.rib.core.RibInteractor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;

/** The processor pipeline for {@link RibInteractor} */
public class RibInteractorProcessorPipeline extends TypeProcessorPipeline {

  public static final Class<RibInteractor> SUPPORT_ANNOTATION_TYPE = RibInteractor.class;

  private AnnotationVerifier<InteractorAnnotatedClass> annotationVerifier;
  @Nullable private Generator<InteractorAnnotatedClass> interactorGenerator;
  private List<InteractorAnnotatedClass> builderAnnotatedClassesList = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param processContext the {@link ProcessContext}.
   * @param interactorGenerator the code generator.
   */
  public RibInteractorProcessorPipeline(
      ProcessContext processContext,
      @Nullable Generator<InteractorAnnotatedClass> interactorGenerator) {
    super(processContext);
    annotationVerifier = new InteractorAnnotationVerifier(errorReporter, elementUtils, typesUtils);
    this.interactorGenerator = interactorGenerator;
  }

  /** @return the annotation type this processor pipeline deals with. */
  @Override
  public Class<? extends Annotation> getAnnotationType() {
    return SUPPORT_ANNOTATION_TYPE;
  }

  /**
   * Process the type elements.
   *
   * @param annotatedTypes annotation types.
   * @throws Throwable exception during annotation process.
   */
  @Override
  protected void processTypeElements(List<TypeElement> annotatedTypes) throws Throwable {
    parseTypeElements(annotatedTypes);
    generateSource();
  }

  /** Generate the source code. */
  protected void generateSource() throws IOException {
    if (interactorGenerator == null) {
      return;
    }
    for (InteractorAnnotatedClass interactorAnnotatedClass : builderAnnotatedClassesList) {
      interactorGenerator.generate(interactorAnnotatedClass);
    }
  }

  /**
   * Verifies the element and get {@link InteractorAnnotatedClass} info.
   *
   * @param annotatedTypes the annotated types.
   */
  protected void parseTypeElements(List<TypeElement> annotatedTypes) throws Throwable {
    for (TypeElement typeElement : annotatedTypes) {
      InteractorAnnotatedClass builderAnnotatedClass = annotationVerifier.verify(typeElement);
      builderAnnotatedClassesList.add(builderAnnotatedClass);
    }
  }
}
