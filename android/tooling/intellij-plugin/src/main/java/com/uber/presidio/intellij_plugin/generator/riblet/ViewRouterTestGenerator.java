package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class ViewRouterTestGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletViewRouterTest.java.template";

  public ViewRouterTestGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouterTest", getRibletName());
  }
}
