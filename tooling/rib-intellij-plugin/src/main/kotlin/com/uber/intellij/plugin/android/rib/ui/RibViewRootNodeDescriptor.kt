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

import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.psi.PsiElement
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils
import com.uber.intellij.plugin.android.rib.io.RibNode
import com.uber.intellij.plugin.android.rib.io.RibView
import java.awt.Font

/**
 * Node descriptor used to render view tree.
 *
 * @param nonNullProject the current project
 * @param element the psi element corresponding to this descriptor
 * @param ribNode the rib node corresponding to this descriptor
 * @param ribView the rib view corresponding to this descriptor
 */
public class RibViewRootNodeDescriptor(
  private val nonNullProject: Project,
  element: PsiElement,
  ribNode: RibNode,
  ribView: RibView?,
) : RibViewNodeDescriptor(nonNullProject, element, ribNode, ribView) {

  override fun updateText(text: CompositeAppearance) {
    if (ribView?.name?.isNotEmpty() == false) {
      text.ending.addText("Please select a RIB", getDefaultTextAttributes())
      return
    }
    text.ending.addText(
      RibHierarchyUtils.formatSimpleName(ribNode.name).replace("Router", "View"),
      getDefaultTextAttributes(),
    )
    if (ribView != null && ribView.layoutId.isNotEmpty()) {
      val boldFont = TextAttributes(myColor, null, null, null, Font.BOLD)
      text.ending.addText(" [layout/${ribView.layoutId}.xml]", boldFont)
    }
  }
}
