package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

public class OptionalViewExtensionGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibViewOptionalExtension.java.template";

  public OptionalViewExtensionGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sOptionalExtension", getRibName());
  }
}
