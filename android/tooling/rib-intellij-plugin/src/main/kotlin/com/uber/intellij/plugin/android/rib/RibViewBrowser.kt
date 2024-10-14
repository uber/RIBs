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

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.ide.hierarchy.JavaHierarchyUtil
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.presentation.java.ClassPresentationUtil
import com.intellij.ui.treeStructure.Tree
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.isRootElement
import com.uber.intellij.plugin.android.rib.io.RibNode
import com.uber.intellij.plugin.android.rib.io.RibView
import com.uber.intellij.plugin.android.rib.ui.HierarchyBrowserBase
import com.uber.intellij.plugin.android.rib.ui.RibHierarchyTreeStructure
import com.uber.intellij.plugin.android.rib.ui.RibViewNodeDescriptor
import com.uber.intellij.plugin.android.rib.ui.RibViewRootNodeDescriptor
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.MessageFormat
import java.util.UUID
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.Comparator

/** UI component used to render tree of Ribs. */
@SuppressWarnings("TooManyFunctions")
public class RibViewBrowser(
  project: Project,
  private val model: Model,
  private val rootElement: PsiElement,
  private val selectionListener: Listener?,
) : HierarchyBrowserBase(project, rootElement) {

  public companion object {
    /** Go to previous Rib label */
    public const val LABEL_GO_PREVIOUS_RIB: String = "Go to previous Scope."

    /** Go to next Rib label */
    public const val LABEL_GO_NEXT_RIB: String = "Go to next Scope"

    /** Type of the Rib hierarchy */
    public const val TYPE_HIERARCHY_TYPE: String = "Views"
  }

  /**
   * Data class used to represent the host of the Rib application
   *
   * @param ribNode the rib node corresponding to this model
   * @param ribView the rib view corresponding to this model
   * @param rootRib the root rib corresponding to this model
   * @param selectedRibId the RIB ID of the RIB selected by user (if any)
   * @param selectedViewId the view ID of the view selected by user (if any)
   */
  public data class Model(
    val ribNode: RibNode,
    val ribView: RibView,
    val rootRib: RibNode,
    val selectedRibId: String = "",
    val selectedViewId: String = "",
  )

  private var hasFocus: Boolean = false

  override fun isApplicableElement(element: PsiElement): Boolean = element is PsiClass

  override fun getActionPlace(): String = ActionPlaces.METHOD_HIERARCHY_VIEW_TOOLBAR

  override fun getComparator(): Comparator<NodeDescriptor<*>> =
    JavaHierarchyUtil.getComparator(myProject)

  override fun getElementFromDescriptor(descriptor: HierarchyNodeDescriptor): PsiElement? {
    if (isRootElement(descriptor.psiElement)) {
      return null
    }
    return descriptor.psiElement
  }

  override fun getPrevOccurenceActionNameImpl(): String = LABEL_GO_PREVIOUS_RIB

  override fun createLegendPanel(): JPanel? = null

  override fun createTrees(trees: MutableMap<in String, in JTree>) {
    trees[TYPE_HIERARCHY_TYPE] = createTree(true)
  }

  override fun getNextOccurenceActionNameImpl(): String = LABEL_GO_NEXT_RIB

  override fun getContentDisplayName(typeName: String, element: PsiElement): String? {
    if (element !is PsiClass) {
      return null
    }
    return MessageFormat.format(typeName, ClassPresentationUtil.getNameForClass(element, false))
  }

  override fun appendActions(actionGroup: DefaultActionGroup, helpID: String?) {
    // Do nothing instead of invoking parent method, in order to remove action bar
  }

  override fun createHierarchyTreeStructure(
    typeName: String,
    psiElement: PsiElement,
  ): HierarchyTreeStructure? {
    val rootDescriptor =
      RibViewRootNodeDescriptor(project, psiElement, model.ribNode, model.ribView)
    return RibHierarchyTreeStructure(project, rootDescriptor)
  }

  override fun configureTree(tree: Tree) {
    super.configureTree(tree)

    tree.addFocusListener(
      object : FocusListener {
        override fun focusLost(e: FocusEvent?) {
          hasFocus = false
        }

        override fun focusGained(e: FocusEvent?) {
          hasFocus = true
          notifySelectedViewChanged()
        }
      }
    )

    tree.addTreeSelectionListener { notifySelectedViewChanged() }

    // by default, expand the entire tree, and select item if needed
    ApplicationManager.getApplication().invokeLater {
      expandAll()
      if (model.selectedViewId.isNotEmpty() && this.model.ribNode.id == this.model.selectedRibId) {
        val ribView: RibView =
          RibHierarchyUtils.findRibViewRecursive(
            this.model.rootRib.view,
            UUID.fromString(model.selectedViewId),
          ) ?: return@invokeLater
        selectionListener?.onSelectedViewChanged(ribView)

        ApplicationManager.getApplication().invokeLater { selectById(model.selectedViewId) }
      }
    }
  }

  /** Notify that the currently selected view has changed. */
  public fun notifySelectedViewChanged() {
    val node: Any? = currentTree.lastSelectedPathComponent
    if (node is DefaultMutableTreeNode && hasFocus) {
      val descriptor = node.userObject
      if (
        descriptor is RibViewNodeDescriptor &&
          descriptor.ribView != null &&
          descriptor.ribView.id.isNotEmpty()
      ) {
        selectionListener?.onSelectedViewChanged(descriptor.ribView)
      }
    }
  }

  /**
   * Interface used to notify that a new view was selected in {@ScopeHierarchyBrowser} component.
   */
  public interface Listener {

    /** Callback indicating the selected View has changed. */
    public fun onSelectedViewChanged(ribView: RibView)
  }
}
