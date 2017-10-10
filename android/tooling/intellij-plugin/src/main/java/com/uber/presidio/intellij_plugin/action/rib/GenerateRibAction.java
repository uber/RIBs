package com.uber.presidio.intellij_plugin.action.rib;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.uber.presidio.intellij_plugin.generator.GeneratorPair;

/**
 * This action will ask generate files required for a new Rib based on a name provided by the
 * user.
 */
public class GenerateRibAction extends GenerateAction implements GenerateRibDialog.Listener {

  @Override
  public void actionPerformed(AnActionEvent anActionEvent) {
    GenerateRibDialog dialog = new GenerateRibDialog(this);
    dialog.show();
  }

  @Override
  public void onGenerateClicked(String ribName, boolean createPresenterAndView) {
    final GeneratorPair generators =
        createPresenterAndView
            ? Generators.getGeneratorsForRibWithPresenterAndView(getPackageName(), ribName)
            : Generators.getGeneratorsForRibWithoutPresenterAndView(
                getPackageName(), ribName);
    generate(generators.getMainSourceSetGenerators(), generators.getTestSourceSetGenerators());
  }
}
