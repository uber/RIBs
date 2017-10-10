package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class ViewRouterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletViewRouter.java.template";

  public ViewRouterGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouter", getRibletName());
  }
}
