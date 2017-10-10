package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class ViewBuilderGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletViewBuilder.java.template";

  public ViewBuilderGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sBuilder", getRibletName());
  }
}
