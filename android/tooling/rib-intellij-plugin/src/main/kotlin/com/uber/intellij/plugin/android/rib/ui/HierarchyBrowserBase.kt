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
package com.uber.intellij.plugin.android.rib.ui

import com.intellij.ide.hierarchy.HierarchyBrowserBaseEx
import com.intellij.ide.hierarchy.HierarchyBrowserManager
import com.intellij.ide.hierarchy.HierarchyNodeRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.ui.AutoScrollToSourceHandler
import com.intellij.ui.TreeSpeedSearch
import com.intellij.ui.tree.TreeVisitor
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

/**
 * Base class for hierarchy browser UI components.
 *
 * It enables speed search configurations to search non-expanded nodes too. It is needed when
 * searching for given scope(s) in the entire graph hierarchy (which can be pretty large).
 */
public abstract class HierarchyBrowserBase(
  public val project: Project,
  private val rootElement: PsiElement,
) : HierarchyBrowserBaseEx(project, rootElement) {

  override fun doRefresh(currentBuilderOnly: Boolean) {
    ApplicationManager.getApplication().invokeLater {
      super.doRefresh(currentBuilderOnly)
      ApplicationManager.getApplication().invokeLater {
        expandAll()
        onRefreshComplete()
      }
    }
  }

  override fun configureTree(tree: Tree) {
    // Hack: we're copying code from parent class here, in order to override speed search behavior
    tree.selectionModel.selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
    tree.toggleClickCount = -1
    tree.cellRenderer = HierarchyNodeRenderer()
    TreeSpeedSearch(tree, { path -> path.lastPathComponent.toString() }, true)
    TreeUtil.installActions(tree)
    object : AutoScrollToSourceHandler() {
        override fun isAutoScrollMode(): Boolean {
          return HierarchyBrowserManager.getSettings(myProject).IS_AUTOSCROLL_TO_SOURCE
        }

        override fun setAutoScrollMode(state: Boolean) {
          HierarchyBrowserManager.getSettings(myProject).IS_AUTOSCROLL_TO_SOURCE = state
        }
      }
      .install(tree)
  }

  /** Refresh completion callback. */
  public open fun onRefreshComplete() {}

  /** Expand entire hierarchy */
  public fun expandAll() {
    TreeUtil.expandAll(currentTree)
  }

  /** Select a given item in the hierarchy, based on provided id. */
  public fun selectById(id: String) {
    TreeUtil.promiseSelect(
      currentTree,
      object : TreeVisitor {
        override fun visit(path: TreePath): TreeVisitor.Action {
          if (path.lastPathComponent is DefaultMutableTreeNode) {
            val node: DefaultMutableTreeNode = path.lastPathComponent as DefaultMutableTreeNode
            if (node.userObject is RibHierarchyDescriptor) {
              val descriptor = node.userObject as RibHierarchyDescriptor
              if (descriptor.getUniqueId() == id) {
                return TreeVisitor.Action.INTERRUPT
              }
            }
          }
          return TreeVisitor.Action.CONTINUE
        }
      },
    )
  }
}
