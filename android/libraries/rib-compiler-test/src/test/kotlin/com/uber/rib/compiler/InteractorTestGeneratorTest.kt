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
package com.uber.rib.compiler;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

import javax.tools.JavaFileObject;
import org.junit.Test;

public class InteractorTestGeneratorTest extends InteractorTestGeneratorProcessorTestBase {

  @Test
  public void processor_withAnInteractor_shouldGenerateTestHelper() {
    JavaFileObject expectedTestCreator = getResourceFile("fixtures/TestAnnotatedInteractor.java");

    addResourceToSources("fixtures/AnnotatedInteractor.java");
    assert_()
        .about(javaSources())
        .that(getSources())
        .withCompilerOptions("-source", "1.7", "-target", "1.7")
        .processedWith(getRibInteractorProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedTestCreator);
  }

  @Test
  public void processor_withABasicInteractor_shouldGenerateTestHelper() {
    JavaFileObject expectedTestCreator =
        getResourceFile("fixtures/TestAnnotatedBasicInteractor.java");

    addResourceToSources("fixtures/AnnotatedBasicInteractor.java");
    assert_()
        .about(javaSources())
        .that(getSources())
        .withCompilerOptions("-source", "1.7", "-target", "1.7")
        .processedWith(getRibInteractorProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedTestCreator);
  }
}
