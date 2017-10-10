package com.uber.presidio.intellij_plugin.generator.rib;

import com.uber.presidio.intellij_plugin.generator.Generator;

/** Generate subclass that uses a view template. */
public class ViewGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibView.java.template";

  public ViewGenerator(String packageName, String ribName) {
    super(packageName, ribName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sView", getRibName());
  }
}
