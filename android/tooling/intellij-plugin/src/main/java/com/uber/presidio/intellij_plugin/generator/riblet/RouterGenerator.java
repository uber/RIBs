package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class RouterGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletRouter.java.template";

  public RouterGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sRouter", getRibletName());
  }
}
