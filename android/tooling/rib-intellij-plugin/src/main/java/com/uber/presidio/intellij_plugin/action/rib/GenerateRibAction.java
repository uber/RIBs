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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.uber.presidio.intellij_plugin.generator.GeneratorPair;

/**
 * This action will ask generate files required for a new Rib based on a name provided by the user.
 */
public class GenerateRibAction extends GenerateAction implements GenerateRibDialog.Listener {

  @Override
  public void actionPerformed(AnActionEvent anActionEvent) {
    GenerateRibDialog dialog = new GenerateRibDialog(this);
    dialog.show();
  }

  @Override
  public void onGenerateClicked(
      String ribName, boolean createPresenterAndView, boolean isKotlinSelected) {
    final GeneratorPair generators =
        createPresenterAndView
            ? Generators.getGeneratorsForRibWithPresenterAndView(
                getPackageName(), ribName, isKotlinSelected)
            : Generators.getGeneratorsForRibWithoutPresenterAndView(
                getPackageName(), ribName, isKotlinSelected);
    generate(generators.getMainSourceSetGenerators(), generators.getTestSourceSetGenerators());
  }
}
