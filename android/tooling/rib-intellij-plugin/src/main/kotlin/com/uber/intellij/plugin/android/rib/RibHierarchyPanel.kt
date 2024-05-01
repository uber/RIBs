/*
 * Copyright (C) 2018-2019. Uber Technologies
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
package com.uber.intellij.plugin.android.rib

import com.android.ddmlib.IDevice
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.uber.intellij.plugin.android.rib.RibHierarchyBrowser.Model
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.findRibNodeRecursive
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.findRibViewRecursive
import com.uber.intellij.plugin.android.rib.RibViewBrowser.Model as ViewModel
import com.uber.intellij.plugin.android.rib.io.RibNode
import com.uber.intellij.plugin.android.rib.io.RibView
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.UUID
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JSplitPane.RIGHT

/** UI Component representing the panel including rib hierarchy. */
public class RibHierarchyPanel(public val project: Project, private val initialModel: Model) :
  JPanel(),
  RibProjectService.Listener,
  ActionListener,
  RibHierarchyBrowser.Listener,
  RibViewBrowser.Listener {

  public companion object {
    private val EMPTY_RIB_VIEW: RibView = RibView("", "", "", "", emptyList())
    private val EMPTY_RIB_NODE: RibNode = RibNode("", "", emptyList(), EMPTY_RIB_VIEW)
    private val EMPTY_VIEW_MODEL: ViewModel =
      RibViewBrowser.Model(EMPTY_RIB_NODE, EMPTY_RIB_VIEW, EMPTY_RIB_NODE)
  }

  private val ribProjectService: RibProjectService = project.service()
  private val comboBox: JComboBox<IDevice>
  private val comboBoxModel: DefaultComboBoxModel<IDevice>
  private val splitPane: JSplitPane
  private val ribBrowser: RibHierarchyBrowser
  private var viewBrowser: RibViewBrowser? = null
  private var model: Model = initialModel
  private var dividerSet: Boolean = false

  init {
    val rootElement: PsiElement = RibHierarchyUtils.buildRootElement(project)

    // Build UI
    layout = GridLayoutManager(2, 1, Insets(0, 0, 0, 0), -1, -1)

    comboBoxModel = DefaultComboBoxModel()
    comboBox = JComboBox(comboBoxModel)
    comboBox.addActionListener(this)
    add(
      comboBox,
      GridConstraints(
        0,
        0,
        1,
        1,
        GridConstraints.ANCHOR_WEST,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED,
        null,
        null,
        null,
        0,
        false,
      ),
    )

    ribBrowser = RibHierarchyBrowser(project, model, rootElement, this)
    ribBrowser.changeView(RibHierarchyBrowser.TYPE_HIERARCHY_TYPE)
    splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, ribBrowser, null)
    add(
      splitPane,
      GridConstraints(
        1,
        0,
        1,
        1,
        GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_BOTH,
        GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
        null,
        null,
        null,
        0,
        false,
      ),
    )
  }

  /** Requests to update the list of devices. */
  public fun onAvailableDevicesChanged(devices: List<IDevice>) {
    comboBoxModel.removeAllElements()
    devices.forEach { comboBoxModel.addElement(it) }
  }

  /** Requests to update the selected device. */
  public fun onSelectedDeviceChanged(selectedDevice: IDevice?) {
    comboBoxModel.selectedItem = selectedDevice
  }

  /** Listbox callback. */
  override fun actionPerformed(e: ActionEvent) {
    if (e.actionCommand == "comboBoxChanged") {
      ribProjectService.selectDevice(comboBox.selectedItem as IDevice?)
    }
  }

  /** Updates the UI with provided model. */
  override fun onModelUpdated(model: Model) {
    this.model = model
    ribBrowser.onModelUpdated(model)
    splitPane.add(JPanel(), RIGHT)
    viewBrowser = null
  }

  /** Notify panel that a new rib has been selected in rib hierarchy browser. */
  override fun onSelectedRibChanged(id: UUID) {
    if (model.selectedViewId.isEmpty()) {
      ribProjectService.highlightRib(id)
    }

    val rootElement: PsiElement = RibHierarchyUtils.buildRootElement(project)
    val rootRibNode: RibNode = this.model.host.application?.activities?.first()?.rootRib ?: return
    val ribNode: RibNode? = findRibNodeRecursive(rootRibNode, id)
    val ribView: RibView? =
      if (ribNode?.view?.id?.isNotEmpty() == true) {
        findRibViewRecursive(rootRibNode.view, UUID.fromString(ribNode?.view?.id))
      } else {
        null
      }
    val model =
      if (ribNode != null && ribView != null) {
        ViewModel(
          ribNode,
          ribView,
          rootRibNode,
          this.model.selectedRibId,
          this.model.selectedViewId,
        )
      } else {
        EMPTY_VIEW_MODEL
      }

    val previousDividerLocation = splitPane.dividerLocation
    viewBrowser = RibViewBrowser(project, model, rootElement, this)
    viewBrowser?.changeView(RibViewBrowser.TYPE_HIERARCHY_TYPE)

    splitPane.add(viewBrowser, RIGHT)
    if (ribView != null && !dividerSet) {
      splitPane.dividerLocation = splitPane.height / 2
      dividerSet = true
    } else {
      splitPane.dividerLocation = previousDividerLocation
    }
  }

  /** Notify panel that a new view has been selected in rib hierarchy browser. */
  override fun onSelectedViewChanged(ribView: RibView) {
    ribProjectService.highlightView(UUID.fromString(ribView.id))
  }
}
