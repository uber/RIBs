package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class RouterTestGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibRouterTest.java.template";

  public RouterTestGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouterTest", getRibName());
  }
}
