package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class RouterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibRouter.java.template";

  public RouterGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouter", getRibName());
  }
}
