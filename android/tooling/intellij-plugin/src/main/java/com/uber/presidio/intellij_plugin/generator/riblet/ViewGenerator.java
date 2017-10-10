package com.uber.presidio.intellij_plugin.generator.riblet;

import com.uber.presidio.intellij_plugin.generator.Generator;

/** Generate subclass that uses a view template. */
public class ViewGenerator extends Generator {

  private static final String TEMPLATE_NAME = "RibletView.java.template";

  public ViewGenerator(String packageName, String ribletName) {
    super(packageName, ribletName, TEMPLATE_NAME);
  }

  @Override
  public String getClassName() {
    return String.format("%sView", getRibletName());
  }
}
