package com.uber.checker.rx;

import com.google.errorprone.CompilationTestHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
@SuppressWarnings("CheckTestExtendsBaseClass")
public class RxJavaMissingAutodisposeErrorCheckerTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private CompilationTestHelper compilationHelper;

  @Before
  public void setup() {
    compilationHelper =
        CompilationTestHelper.newInstance(RxJavaMissingAutodisposeErrorChecker.class, getClass());
    compilationHelper.setArgs(Arrays.asList("-d", temporaryFolder.getRoot().getAbsolutePath()));
  }

  @Test
  public void test_autodisposePositiveCases() {
    compilationHelper.addSourceFile("MissingAutodisposeErrorPositiveCases.java").doTest();
  }

  @Test
  public void test_autodisposeNegativeCases() {
    compilationHelper.addSourceFile("MissingAutodisposeErrorNegativeCases.java").doTest();
  }
}
