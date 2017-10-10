package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class OptionalViewExtensionGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletViewOptionalExtension.java.template";

  public OptionalViewExtensionGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sOptionalExtension", getRibletName());
  }
}
