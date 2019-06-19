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
 package com.uber.presidio.intellij_plugin.action.rib;

import androidx.annotation.NonNull;

import com.google.testing.compile.JavaFileObjects;
import com.uber.presidio.intellij_plugin.generator.Generator;
import com.uber.presidio.intellij_plugin.generator.GeneratorPair;
import com.uber.rib.compiler.RibTestProcessor;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaFileObject;

import dagger.internal.codegen.ComponentProcessor;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class RibGeneratorsTest {

  private static final String TEST_PACKAGE_NAME = "com.test";
  private static final String TEST_RIBLET_NAME = "TestRib";

  @Test
  public void ribGenerators_shouldGenerateClassesThatCompiler() {
    testWithGenerators(Generators.getGeneratorsForRibWithoutPresenterAndView(TEST_PACKAGE_NAME, TEST_RIBLET_NAME, false));
  }

  @Test
  @Ignore("This test only passes when using buck+okbuck. Gradle is unable to import the rib-android aar as a test "
      + "dependency for this java module.")
  public void viewRibGenerators_shouldGenerateClassesThatCompile() {
    testWithGenerators(Generators.getGeneratorsForRibWithPresenterAndView(TEST_PACKAGE_NAME, TEST_RIBLET_NAME, false));
  }

  private static void testWithGenerators(@NonNull GeneratorPair generators) {
    List<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>();

    generateSourceFiles(javaFileObjects, generators.getMainSourceSetGenerators());
    generateSourceFiles(javaFileObjects, generators.getTestSourceSetGenerators());

    assert_()
            .about(javaSources())
            .that(javaFileObjects)
            .processedWith(new ComponentProcessor(), new RibTestProcessor())
            .compilesWithoutError();
  }

  private static void generateSourceFiles(
          List<JavaFileObject> javaFileObjects, List<Generator> generators) {
    for (Generator generator : generators) {
      javaFileObjects.add(
              JavaFileObjects.forSourceString(
                      String.format("%s.%s", generator.getPackageName(), generator.getClassName()),
                      generator.generate()));
    }
  }
}
