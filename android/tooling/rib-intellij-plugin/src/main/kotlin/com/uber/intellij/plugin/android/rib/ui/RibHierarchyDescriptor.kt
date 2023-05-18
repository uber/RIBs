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

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.openapi.util.Comparing
import com.intellij.psi.PsiElement
import java.awt.Color
import java.awt.Font
import javax.swing.Icon

/** Base class for all tree node descriptors used in Rib IntelliJ plugin. */
public open class RibHierarchyDescriptor(
  project: Project,
  public val parentDescriptor: HierarchyNodeDescriptor?,
  public val element: PsiElement,
  isBase: Boolean,
) : HierarchyNodeDescriptor(project, parentDescriptor, element, isBase) {

  /** Method to set text of the node entry. */
  public open fun updateText(text: CompositeAppearance) {}

  /** Method used to get the unique id of descriptor. Used for programmatic selection. */
  public open fun getUniqueId(): String? {
    return null
  }

  override fun update(): Boolean {
    val changes = super.update()

    if (psiElement == null) {
      return invalidElement()
    }

    val oldText = myHighlightedText
    myHighlightedText = CompositeAppearance()
    updateText(myHighlightedText)

    return changes || !myHighlightedText.compareTo(oldText)
  }

  /** Return default text attributes. */
  public fun getDefaultTextAttributes(isError: Boolean = false): TextAttributes {
    val font: Int = if (myIsBase) Font.BOLD else Font.PLAIN
    return if (isError) {
      TextAttributes(myColor, null, Color.red, EffectType.WAVE_UNDERSCORE, font)
    } else {
      TextAttributes(myColor, null, null, null, font)
    }
  }

  /** Return icon to display. */
  override fun getIcon(element: PsiElement): Icon? {
    return null
  }

  /** Compare 2 instances of text appearance. */
  public fun CompositeAppearance.compareTo(another: CompositeAppearance): Boolean {
    return Comparing.equal(this, another)
  }
}
