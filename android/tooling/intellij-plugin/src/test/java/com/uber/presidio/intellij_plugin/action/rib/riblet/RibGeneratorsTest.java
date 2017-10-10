package com.uber.presidio.intellij_plugin.action.rib.riblet;

import android.support.annotation.NonNull;

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
  public void ribletGenerators_shouldGenerateClassesThatCompiler() {
    testWithGenerators(Generators.getGeneratorsForRibletWithoutPresenterAndView(TEST_PACKAGE_NAME, TEST_RIBLET_NAME));
  }

  @Test
  @Ignore("This test only passes when using buck+okbuck. Gradle is unable to import the rib-android aar as a test "
      + "dependency for this java module.")
  public void viewRibletGenerators_shouldGenerateClassesThatCompile() {
    testWithGenerators(Generators.getGeneratorsForRibletWithPresenterAndView(TEST_PACKAGE_NAME, TEST_RIBLET_NAME));
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
