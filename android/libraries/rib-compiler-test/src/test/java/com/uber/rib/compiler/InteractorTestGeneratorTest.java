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

import org.junit.Ignore;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class InteractorTestGeneratorTest extends InteractorTestGeneratorProcessorTestBase {

  @Ignore("The maven coordinate com.sun:tools no longer appears to work. So this test won't pass.")
  @Test
  public void processor_withAnInteractor_shouldCreateANewScopeAnnotation() {
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
}
