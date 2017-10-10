package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class BuilderGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibBuilder.java.template";

  public BuilderGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sBuilder", getRibName());
  }
}
