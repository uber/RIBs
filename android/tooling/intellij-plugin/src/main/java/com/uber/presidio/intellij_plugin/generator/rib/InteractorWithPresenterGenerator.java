package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class InteractorWithPresenterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibInteractorWithPresenter.java.template";

  public InteractorWithPresenterGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sInteractor", getRibName());
  }
}
