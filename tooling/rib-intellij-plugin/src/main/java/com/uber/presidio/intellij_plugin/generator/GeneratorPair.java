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
