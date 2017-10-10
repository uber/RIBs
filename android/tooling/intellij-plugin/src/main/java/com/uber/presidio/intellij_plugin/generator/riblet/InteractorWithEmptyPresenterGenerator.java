package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class InteractorWithEmptyPresenterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletInteractorWithEmptyPresenter.java.template";

  public InteractorWithEmptyPresenterGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sInteractor", getRibletName());
  }
}
