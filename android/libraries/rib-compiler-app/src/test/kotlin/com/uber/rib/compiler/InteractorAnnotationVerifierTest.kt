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

import com.google.common.truth.Truth
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.Test

class InteractorAnnotationVerifierTest : InteractorProcessorTestBase() {

  @Test
  fun verify_whenTypeElementIsValid_shouldCompile() {
    addResourceToSources("fixtures/AnnotatedInteractor.java")
    assertCompiles()
  }

  @Test
  fun verify_whenTypeElementIsInteractorWithoutProperSuffix_shouldWriteErrorMessage() {
    addResourceToSources("fixtures/AnnotatedInteractorNoSuffix.java")
    assertFailsWithError("test.AnnotatedInteractorNoSuffix does not end in Interactor.")
  }

  @Test
  fun verify_whenInteractorHasAConstructor_shouldCompile() {
    addResourceToSources("fixtures/CustomConstructorInteractor.java")
    assertCompiles()
  }

  @Test
  fun verify_whenTypeElementIsNotInteractor_shouldWriteErrorMessage() {
    addResourceToSources("fixtures/AnnotatedNonInteractor.java")
    assertFailsWithError(
      "test.AnnotatedNonInteractor is annotated with @RibInteractor but is not an Interactor subclass",
    )
  }

  private fun assertFailsWithError(expectedErrorMessage: String) {
    Truth.assert_()
      .about(JavaSourcesSubjectFactory.javaSources())
      .that(sources)
      .withCompilerOptions("-source", "1.7", "-target", "1.7")
      .processedWith(ribProcessor)
      .failsToCompile()
      .withErrorContaining(expectedErrorMessage)
  }

  private fun assertCompiles() {
    Truth.assert_()
      .about(JavaSourcesSubjectFactory.javaSources())
      .that(sources)
      .withCompilerOptions("-source", "1.7", "-target", "1.7")
      .processedWith(ribProcessor)
      .compilesWithoutError()
  }
}
