package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class BuilderGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletBuilder.java.template";

  public BuilderGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sBuilder", getRibletName());
  }
}
