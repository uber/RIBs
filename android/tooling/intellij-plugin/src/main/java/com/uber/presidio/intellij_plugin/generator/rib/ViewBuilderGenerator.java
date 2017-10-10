package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class ViewBuilderGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibViewBuilder.java.template";

  public ViewBuilderGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sBuilder", getRibName());
  }
}
