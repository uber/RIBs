package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class ViewRouterTestGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibViewRouterTest.java.template";

  public ViewRouterTestGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouterTest", getRibName());
  }
}
