/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.presidio.intellij_plugin.action.rib;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;

/** Dialog that prompts the user for information required to generate a new rib. */
public class GenerateRibDialog extends DialogWrapper {

  private final Listener listener;
  private JPanel contentPane;
  private JTextField ribNameTextField;
  private JCheckBox createPresenterAndViewCheckBox;
  private JCheckBox createKotlinCode;

  public GenerateRibDialog(final Listener listener) {
    super((Project) null);
    this.listener = listener;
    init();

    createPresenterAndViewCheckBox.setSelected(true);
    createKotlinCode.setSelected(true);
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  @Override
  protected void doOKAction() {
    super.doOKAction();

    this.listener.onGenerateClicked(
        ribNameTextField.getText(),
        createPresenterAndViewCheckBox.isSelected(),
        createKotlinCode.isSelected());
  }

  /** Listener interface to be implemented by consumers of the dialog. */
  public interface Listener {

    /**
     * Called when the user clicks OK on the generate dialog.
     *
     * @param ribName name for new rib.
     * @param createPresenterAndView {@code true} when a presenter and a corresponding view should
     *     be created, {@code false} otherwise.
     */
    void onGenerateClicked(
        String ribName, boolean createPresenterAndView, boolean isKotlinSelected);
  }
}
