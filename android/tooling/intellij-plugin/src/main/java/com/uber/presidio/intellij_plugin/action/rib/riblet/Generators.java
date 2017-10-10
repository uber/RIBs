package com.uber.presidio.intellij_plugin.action.rib.riblet;

import com.google.common.collect.ImmutableList;
import com.uber.presidio.intellij_plugin.generator.GeneratorPair;
import com.uber.presidio.intellij_plugin.generator.riblet.BuilderGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.InteractorWithEmptyPresenterGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.InteractorWithEmptyPresenterTestGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.InteractorWithPresenterGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.InteractorWithPresenterTestGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.OptionalExtensionGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.OptionalViewExtensionGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.RouterGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.RouterTestGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.ViewBuilderGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.ViewGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.ViewRouterGenerator;
import com.uber.presidio.intellij_plugin.generator.riblet.ViewRouterTestGenerator;

/** Utility methods for getting lists of generators in different configurations. */
public final class Generators {

  private Generators() {}

  /**
   * @param packageName to use for generators.
   * @param ribletName to use for generators.
   * @return a list of generators to use when generating a riblet with a presenter and view.
   */
  static GeneratorPair getGeneratorsForRibletWithPresenterAndView(
      String packageName, String ribletName) {
    InteractorWithPresenterGenerator interactorGenerator =
        new InteractorWithPresenterGenerator(packageName, ribletName);
    ViewBuilderGenerator viewBuilderGenerator = new ViewBuilderGenerator(packageName, ribletName);
    ViewGenerator viewGenerator = new ViewGenerator(packageName, ribletName);
    ViewRouterGenerator viewRouterGenerator = new ViewRouterGenerator(packageName, ribletName);
    OptionalViewExtensionGenerator extensionGenerator =
        new OptionalViewExtensionGenerator(packageName, ribletName);

    InteractorWithPresenterTestGenerator interactorWithPresenterTestGenerator =
        new InteractorWithPresenterTestGenerator(packageName, ribletName);
    ViewRouterTestGenerator viewRouterTestGenerator =
        new ViewRouterTestGenerator(packageName, ribletName);

    return new GeneratorPair(
        ImmutableList.of(
            interactorGenerator,
            viewBuilderGenerator,
            viewGenerator,
            viewRouterGenerator,
            extensionGenerator),
        ImmutableList.of(interactorWithPresenterTestGenerator, viewRouterTestGenerator));
  }

  /**
   * @param packageName to use for generators.
   * @param ribletName to use for generators.
   * @return a list of generators to use when generating a riblet without a presenter and view.
   */
  static GeneratorPair getGeneratorsForRibletWithoutPresenterAndView(
      String packageName, String ribletName) {
    InteractorWithEmptyPresenterGenerator interactorGenerator =
        new InteractorWithEmptyPresenterGenerator(packageName, ribletName);
    BuilderGenerator builderGenerator = new BuilderGenerator(packageName, ribletName);
    OptionalExtensionGenerator extensionGenerator =
        new OptionalExtensionGenerator(packageName, ribletName);
    RouterGenerator routerGenerator = new RouterGenerator(packageName, ribletName);

    InteractorWithEmptyPresenterTestGenerator interactorWithEmptyPresenterTestGenerator =
        new InteractorWithEmptyPresenterTestGenerator(packageName, ribletName);
    RouterTestGenerator routerTestGenerator = new RouterTestGenerator(packageName, ribletName);

    return new GeneratorPair(
        ImmutableList.of(
            interactorGenerator, builderGenerator, routerGenerator, extensionGenerator),
        ImmutableList.of(interactorWithEmptyPresenterTestGenerator, routerTestGenerator));
  }
}
