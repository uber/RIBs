package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class InteractorWithEmptyPresenterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibInteractorWithEmptyPresenter.java.template";

  public InteractorWithEmptyPresenterGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sInteractor", getRibName());
  }
}
