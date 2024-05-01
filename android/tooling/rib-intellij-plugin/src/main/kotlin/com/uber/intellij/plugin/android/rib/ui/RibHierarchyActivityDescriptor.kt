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
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.formatQualifiedName
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils.Companion.formatSimpleName
import com.uber.intellij.plugin.android.rib.io.RibActivity
import javax.swing.Icon

/** Node descriptor used to render a Rib activities. */
public class RibHierarchyActivityDescriptor(
  project: Project,
  parentDescriptor: HierarchyNodeDescriptor?,
  private val clazz: PsiClass,
  public val ribActivity: RibActivity,
) : RibHierarchyDescriptor(project, parentDescriptor, clazz, false) {

  override fun updateText(text: CompositeAppearance) {
    text.ending.addText(formatSimpleName(ribActivity.name), getDefaultTextAttributes())
    text.ending.addText(" (${formatQualifiedName(ribActivity.name)})", getPackageNameAttributes())
  }

  override fun getIcon(element: PsiElement): Icon? {
    return AllIcons.Actions.Execute
  }
}
