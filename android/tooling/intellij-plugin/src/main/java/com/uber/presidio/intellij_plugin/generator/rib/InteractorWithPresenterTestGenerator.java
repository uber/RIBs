package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class InteractorWithPresenterTestGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibInteractorWithPresenterTest.java.template";

  public InteractorWithPresenterTestGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sInteractorTest", getRibName());
  }
}
