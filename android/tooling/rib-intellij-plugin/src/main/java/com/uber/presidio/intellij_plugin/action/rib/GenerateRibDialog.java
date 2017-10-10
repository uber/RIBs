package com.uber.presidio.intellij_plugin.action.rib;

import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Dialog that prompts the user for information required to generate a new rib. */
public class GenerateRibDialog extends DialogWrapper {

  private JPanel contentPane;
  private JTextField ribNameTextField;
  private JCheckBox createPresenterAndViewCheckBox;

  private final Listener listener;

  public GenerateRibDialog(final Listener listener) {
    super(null);
    this.listener = listener;
    init();

    createPresenterAndViewCheckBox.setSelected(true);
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
        ribNameTextField.getText(), createPresenterAndViewCheckBox.isSelected());
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
    void onGenerateClicked(String ribName, boolean createPresenterAndView);
  }
}
