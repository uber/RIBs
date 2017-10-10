package com.uber.presidio.intellij_plugin.generator;

import java.util.List;

public class GeneratorPair {

  private final List<Generator> mainSourceSetGenerators;
  private final List<Generator> testSourceSetGenerators;

  public GeneratorPair(
      List<Generator> mainSourceSetGenerators, List<Generator> testSourceSetGenerators) {
    this.mainSourceSetGenerators = mainSourceSetGenerators;
    this.testSourceSetGenerators = testSourceSetGenerators;
  }

  public List<Generator> getMainSourceSetGenerators() {
    return mainSourceSetGenerators;
  }

  public List<Generator> getTestSourceSetGenerators() {
    return testSourceSetGenerators;
  }
}
