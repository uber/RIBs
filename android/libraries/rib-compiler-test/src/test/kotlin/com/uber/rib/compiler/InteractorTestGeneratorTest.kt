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

class InteractorTestGeneratorTest : InteractorTestGeneratorProcessorTestBase() {
  @Test
  fun processor_withAnInteractor_shouldGenerateTestHelper() {
    val expectedTestCreator = getResourceFile("fixtures/TestAnnotatedInteractor.java")
    addResourceToSources("fixtures/AnnotatedInteractor.java")
    Truth.assert_()
      .about(JavaSourcesSubjectFactory.javaSources())
      .that(sources)
      .withCompilerOptions("-source", "1.7", "-target", "1.7")
      .processedWith(ribInteractorProcessor)
      .compilesWithoutError()
      .and()
      .generatesSources(expectedTestCreator)
  }

  @Test
  fun processor_withABasicInteractor_shouldGenerateTestHelper() {
    val expectedTestCreator = getResourceFile("fixtures/TestAnnotatedBasicInteractor.java")
    addResourceToSources("fixtures/AnnotatedBasicInteractor.java")
    Truth.assert_()
      .about(JavaSourcesSubjectFactory.javaSources())
      .that(sources)
      .withCompilerOptions("-source", "1.7", "-target", "1.7")
      .processedWith(ribInteractorProcessor)
      .compilesWithoutError()
      .and()
      .generatesSources(expectedTestCreator)
  }
}
