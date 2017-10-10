package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class InteractorWithPresenterTestGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletInteractorWithPresenterTest.java.template";

  public InteractorWithPresenterTestGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sInteractorTest", getRibletName());
  }
}
