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

import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.ide.IdeBundle
import com.intellij.ide.actions.ExportToTextFileToolbarAction
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.ide.hierarchy.JavaHierarchyUtil
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.presentation.java.ClassPresentationUtil
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.content.tabs.PinToolwindowTabAction
import com.intellij.ui.treeStructure.Tree
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.EMPTY_UUID
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.displayPopup
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.getPsiClass
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.isRootElement
import com.uber.intellij.plugin.android.rib.io.RibHost
import com.uber.intellij.plugin.android.rib.ui.HierarchyBrowserBase
import com.uber.intellij.plugin.android.rib.ui.RibHierarchyNodeDescriptor
import com.uber.intellij.plugin.android.rib.ui.RibHierarchyRootNodeDescriptor
import com.uber.intellij.plugin.android.rib.ui.RibHierarchyTreeStructure
import java.text.MessageFormat
import java.util.UUID
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/** UI component used to render tree of Ribs. */
@SuppressWarnings("TooManyFunctions")
public class RibHierarchyBrowser(
  project: Project,
  initialModel: Model,
  private val rootElement: PsiElement,
  private val selectionListener: Listener?,
) : HierarchyBrowserBase(project, rootElement) {

  public companion object {
    /** Go to previous Rib label */
    public const val LABEL_GO_PREVIOUS_RIB: String = "Go to previous Scope."

    /** Go to next Rib label */
    public const val LABEL_GO_NEXT_RIB: String = "Go to next Scope"

    /** Type of the Rib hierarchy */
    public const val TYPE_HIERARCHY_TYPE: String = "Ribs"

    private const val ENABLE_LOCATE_MODE: String = "Enable selecting RIB on device"

    private const val LOCATE_VIEW: String =
      "Please click on the UI element on your<br>" + "emulator/device locate it."
  }

  /** Enum used to represent the status of the component */
  public enum class Status {
    UNINITIALIZED,
    INITIALIZING,
    INITIALIZED,
    REFRESHING,
  }

  /**
   * Data class used to represent the host of the Rib application and possible selections
   *
   * @param host the model of the application host
   * @param selectedRibId the RIB ID of the RIB selected by user (if any)
   * @param selectedViewId the view ID of the view selected by user (if any)
   */
  public data class Model(
    val host: RibHost,
    val selectedRibId: String = "",
    val selectedViewId: String = "",
  )

  private val ribProjectService: RibProjectService = project.service()

  private var status: Status = Status.UNINITIALIZED

  private var model: Model = initialModel

  private var menuGroup: DefaultActionGroup? = null

  private var refreshComplete: Boolean = false

  private fun isUpdating(): Boolean {
    return status == Status.INITIALIZING || status == Status.REFRESHING
  }

  override fun isApplicableElement(element: PsiElement): Boolean {
    return element is PsiClass
  }

  override fun getActionPlace(): String {
    return ActionPlaces.METHOD_HIERARCHY_VIEW_TOOLBAR
  }

  override fun getComparator(): Comparator<NodeDescriptor<*>> {
    return JavaHierarchyUtil.getComparator(myProject)
  }

  override fun getElementFromDescriptor(descriptor: HierarchyNodeDescriptor): PsiElement? {
    if (isRootElement(descriptor.psiElement)) {
      return null
    }
    return descriptor.psiElement
  }

  override fun getPrevOccurenceActionNameImpl(): String {
    return LABEL_GO_PREVIOUS_RIB
  }

  override fun createLegendPanel(): JPanel? {
    return null
  }

  override fun getNextOccurenceActionNameImpl(): String {
    return LABEL_GO_NEXT_RIB
  }
  override fun createTrees(trees: MutableMap<in String, in JTree>) {
    trees[TYPE_HIERARCHY_TYPE] = createTree(true)
  }

  override fun getContentDisplayName(typeName: String, element: PsiElement): String? {
    if (element !is PsiClass) {
      return null
    }
    return MessageFormat.format(typeName, ClassPresentationUtil.getNameForClass(element, false))
  }

  override fun appendActions(actionGroup: DefaultActionGroup, helpID: String?) {
    super.appendActions(actionGroup, helpID)
    menuGroup = actionGroup

    // replace original refresh action with custom one, so that we can gray it out
    actionGroup.replaceAction(actionGroup.getChildren(null).first(), RefreshAction())

    // remove export/pin action, and add locate, help and autodeploy actions
    actionGroup.remove(actionGroup.getChildren(null).first { it is PinToolwindowTabAction })
    actionGroup.remove(actionGroup.getChildren(null).first { it is ExportToTextFileToolbarAction })
    actionGroup.add(LocateAction())
    actionGroup.add(HelpAction())
  }

  override fun createHierarchyTreeStructure(
    typeName: String,
    psiElement: PsiElement,
  ): HierarchyTreeStructure? {
    if (psiElement == rootElement) {
      val rootDescriptor =
        RibHierarchyRootNodeDescriptor(
          project,
          getPsiClass(project, model.host.name),
          model.host,
          status,
        )
      return RibHierarchyTreeStructure(project, rootDescriptor)
    }
    return null
  }

  override fun configureTree(tree: Tree) {
    super.configureTree(tree)
    tree.addTreeSelectionListener {
      val node: Any? = tree.lastSelectedPathComponent
      if (node is DefaultMutableTreeNode) {
        val descriptor = node.userObject
        if (descriptor is RibHierarchyNodeDescriptor) {
          selectionListener?.onSelectedRibChanged(UUID.fromString(descriptor.ribNode.id))
        } else {
          selectionListener?.onSelectedRibChanged(EMPTY_UUID)
        }
      }
    }
  }

  override fun doRefresh(currentBuilderOnly: Boolean) {
    when (status) {
      Status.INITIALIZED -> {
        status = Status.REFRESHING
      }
      Status.UNINITIALIZED -> {
        status = Status.INITIALIZING
        refresh()
      }
      else -> {}
    }
    ApplicationManager.getApplication().invokeLater { ribProjectService.refreshRibHierarchy() }
  }

  /** Request to update hierarchy with provided model */
  public fun onModelUpdated(model: Model) {
    this.status = Status.INITIALIZED
    this.model = model
    this.refreshComplete = false
    refresh()
  }

  /** Callback invoked when refresh completed */
  override fun onRefreshComplete() {
    if (!refreshComplete) {
      model.selectedRibId?.let { selectById(it) }
      refreshComplete = true
    }
  }

  /*
   * Request view to refresh, which causes most recent rib tree to be used.
   */
  private fun refresh() {
    super.doRefresh(true)
  }

  private inner class RefreshAction internal constructor() :
    com.intellij.ide.actions.RefreshAction(
      IdeBundle.message("action.refresh"),
      IdeBundle.message("action.refresh"),
      AllIcons.Actions.Refresh,
    ) {

    override fun actionPerformed(e: AnActionEvent) {
      doRefresh(false)
    }

    override fun update(event: AnActionEvent) {
      val presentation = event.presentation
      val hasDevices = ribProjectService.hasSelectedDevice()
      presentation.isEnabled = hasDevices && !isUpdating()
    }
  }

  private inner class LocateAction internal constructor() :
    AnAction(ENABLE_LOCATE_MODE, ENABLE_LOCATE_MODE, AllIcons.General.Locate) {

    private var popupDisplayed = false

    override fun actionPerformed(e: AnActionEvent) {
      ribProjectService.enableLocateMode()

      if (!popupDisplayed) {
        displayPopup(
          LOCATE_VIEW,
          RelativePoint.getSouthOf(this@RibHierarchyBrowser),
          MessageType.INFO,
        )
        popupDisplayed = true
      }
    }

    override fun update(event: AnActionEvent) {
      event.presentation.isEnabled = !ribProjectService.isLocating()
    }
  }

  private inner class HelpAction internal constructor() :
    AnAction(
      IdeBundle.message("action.help"),
      IdeBundle.message("action.help"),
      AllIcons.General.TodoQuestion,
    ) {

    override fun actionPerformed(e: AnActionEvent) {
      BrowserUtil.open(
        "https://github.com/uber/RIBs/wiki/Android-Tooling#ribs-intellij-plugin-for-android",
      )
    }

    override fun update(event: AnActionEvent) {
      event.presentation.isEnabled = true
    }
  }

  /**
   * Interface used to notify that an new element was selected in {@ScopeHierarchyBrowser}
   * component.
   */
  public interface Listener {

    /** Callback indicating the selected Rib has changed. */
    public fun onSelectedRibChanged(id: UUID)
  }
}
