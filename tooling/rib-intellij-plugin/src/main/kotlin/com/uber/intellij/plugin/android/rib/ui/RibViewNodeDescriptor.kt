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

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.psi.PsiElement
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils
import com.uber.intellij.plugin.android.rib.io.RibNode
import com.uber.intellij.plugin.android.rib.io.RibView
import java.awt.Font
import javax.swing.Icon

/**
 * Node descriptor used to render view tree.
 *
 * @param project the current project
 * @param element the psi element corresponding to this descriptor
 * @param ribNode the rib node corresponding to this descriptor
 * @param ribView the rib view corresponding to this descriptor
 */
public open class RibViewNodeDescriptor(
  project: Project,
  element: PsiElement,
  public val ribNode: RibNode,
  public val ribView: RibView?,
) : RibHierarchyDescriptor(project, null, element, false) {

  override fun updateText(text: CompositeAppearance) {
    if (ribView == null) {
      return
    }
    text.ending.addText(RibHierarchyUtils.formatSimpleName(ribView.name))

    val boldFont = TextAttributes(myColor, null, null, null, Font.BOLD)
    if (ribView.layoutId.isNotEmpty()) {
      text.ending.addText(" [layout/${ribView.layoutId}.xml]", boldFont)
    } else if (ribView.viewId.isNotEmpty()) {
      text.ending.addText(" [id/${ribView.viewId}]", boldFont)
    }

    if (ribView.name.isNotEmpty()) {
      text.ending.addText(
        " (${RibHierarchyUtils.formatQualifiedName(ribView.name)})",
        getPackageNameAttributes(),
      )
    }
  }

  /** Method used to get the unique id of descriptor. Used for programmatic selection. */
  override fun getUniqueId(): String? {
    return ribView?.id ?: null
  }

  override fun toString(): String {
    return ribView?.viewId ?: ""
  }

  @SuppressWarnings("ReturnCount")
  override fun getIcon(element: PsiElement): Icon? {
    if (ribView?.name == null || ribView.name.isEmpty()) {
      return AllIcons.General.BalloonWarning
    } else if (hasLayoutId()) {
      return AllIcons.General.CopyHovered
    } else if (ribView.name.contains("Image")) {
      return AllIcons.General.LayoutPreviewOnly
    } else if (ribView.name.contains("Text")) {
      return AllIcons.Actions.Highlighting
    } else if (ribView.name.contains("Layout")) {
      return AllIcons.Graph.Grid
    }
    return AllIcons.General.InspectionsEye
  }

  private fun hasLayoutId(): Boolean {
    return ribView != null && ribView.layoutId.isNotEmpty()
  }
}
