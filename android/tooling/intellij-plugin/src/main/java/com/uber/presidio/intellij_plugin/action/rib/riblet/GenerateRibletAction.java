package com.uber.presidio.intellij_plugin.action.rib.riblet;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.uber.presidio.intellij_plugin.action.rib.GenerateAction;
import com.uber.presidio.intellij_plugin.generator.GeneratorPair;

/**
 * This action will ask generate files required for a new Riblet based on a name provided by the
 * user.
 */
public class GenerateRibletAction extends GenerateAction implements GenerateRibletDialog.Listener {

  @Override
  public void actionPerformed(AnActionEvent anActionEvent) {
    GenerateRibletDialog dialog = new GenerateRibletDialog(this);
    dialog.show();
  }

  @Override
  public void onGenerateClicked(String ribletName, boolean createPresenterAndView) {
    final GeneratorPair generators =
        createPresenterAndView
            ? Generators.getGeneratorsForRibletWithPresenterAndView(getPackageName(), ribletName)
            : Generators.getGeneratorsForRibletWithoutPresenterAndView(
                getPackageName(), ribletName);
    generate(generators.getMainSourceSetGenerators(), generators.getTestSourceSetGenerators());
  }
}
