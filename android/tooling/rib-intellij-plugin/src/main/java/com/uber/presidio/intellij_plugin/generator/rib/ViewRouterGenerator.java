package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class ViewRouterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibViewRouter.java.template";

  public ViewRouterGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouter", getRibName());
  }
}
