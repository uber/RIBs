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
package com.uber.presidio.intellij_plugin.action.rib;

import com.google.common.collect.ImmutableList;
import com.uber.presidio.intellij_plugin.generator.GeneratorPair;
import com.uber.presidio.intellij_plugin.generator.rib.BuilderGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.InteractorWithEmptyPresenterGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.InteractorWithEmptyPresenterTestGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.InteractorWithPresenterGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.InteractorWithPresenterTestGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.RouterGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.RouterTestGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.ViewBuilderGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.ViewGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.ViewRouterGenerator;
import com.uber.presidio.intellij_plugin.generator.rib.ViewRouterTestGenerator;

/** Utility methods for getting lists of generators in different configurations. */
public final class Generators {

  private Generators() {}

  /**
   * @param packageName to use for generators.
   * @param ribName to use for generators.
   * @return a list of generators to use when generating a rib with a presenter and view.
   */
  public static GeneratorPair getGeneratorsForRibWithPresenterAndView(
      String packageName, String ribName, boolean isKotlinSelected) {

    InteractorWithPresenterGenerator interactorGenerator =
        new InteractorWithPresenterGenerator(packageName, ribName, isKotlinSelected);
    ViewBuilderGenerator viewBuilderGenerator =
        new ViewBuilderGenerator(packageName, ribName, isKotlinSelected);
    ViewGenerator viewGenerator = new ViewGenerator(packageName, ribName, isKotlinSelected);
    ViewRouterGenerator viewRouterGenerator =
        new ViewRouterGenerator(packageName, ribName, isKotlinSelected);

    InteractorWithPresenterTestGenerator interactorWithPresenterTestGenerator =
        new InteractorWithPresenterTestGenerator(packageName, ribName, isKotlinSelected);
    ViewRouterTestGenerator viewRouterTestGenerator =
        new ViewRouterTestGenerator(packageName, ribName, isKotlinSelected);

    return new GeneratorPair(
        ImmutableList.of(
            interactorGenerator, viewBuilderGenerator, viewGenerator, viewRouterGenerator),
        ImmutableList.of(interactorWithPresenterTestGenerator, viewRouterTestGenerator));
  }

  /**
   * @param packageName to use for generators.
   * @param ribName to use for generators.
   * @return a list of generators to use when generating a rib without a presenter and view.
   */
  public static GeneratorPair getGeneratorsForRibWithoutPresenterAndView(
      String packageName, String ribName, boolean isKotlinSelected) {
    InteractorWithEmptyPresenterGenerator interactorGenerator =
        new InteractorWithEmptyPresenterGenerator(packageName, ribName, isKotlinSelected);
    BuilderGenerator builderGenerator =
        new BuilderGenerator(packageName, ribName, isKotlinSelected);
    RouterGenerator routerGenerator = new RouterGenerator(packageName, ribName, isKotlinSelected);

    InteractorWithEmptyPresenterTestGenerator interactorWithEmptyPresenterTestGenerator =
        new InteractorWithEmptyPresenterTestGenerator(packageName, ribName, isKotlinSelected);
    RouterTestGenerator routerTestGenerator =
        new RouterTestGenerator(packageName, ribName, isKotlinSelected);

    return new GeneratorPair(
        ImmutableList.of(interactorGenerator, builderGenerator, routerGenerator),
        ImmutableList.of(interactorWithEmptyPresenterTestGenerator, routerTestGenerator));
  }
}
