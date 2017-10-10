package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class OptionalExtensionGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibOptionalExtension.java.template";

  public OptionalExtensionGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sOptionalExtension", getRibName());
  }
}
