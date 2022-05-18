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
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.psi.PsiElement
import com.uber.intellij.plugin.android.rib.AndroidDeviceRepositoryComponent
import com.uber.intellij.plugin.android.rib.RibHierarchyBrowser
import com.uber.intellij.plugin.android.rib.RibProjectComponent
import com.uber.intellij.plugin.android.rib.io.RibHost
import javax.swing.Icon

/** Node descriptor used to render tree roots. */
class RibHierarchyRootNodeDescriptor(
  project: Project,
  element: PsiElement,
  val ribHost: RibHost,
  private val status: RibHierarchyBrowser.Status
) : RibHierarchyDescriptor(project, null, element, true) {

  companion object {
    /** Label used when android bridge is not connected */
    const val LABEL_NO_BRIDGE: String =
      "No Android bridge. Make sure Android SDK is configured for this project."

    /** Label used when no device is connected. */
    const val LABEL_NO_DEVICE: String = "No Android device connected..."

    /** Label used when device list is being refreshed. */
    const val LABEL_WAIT: String = "Loading RIB info..."

    /** Label used when no no Rib info could be fetched from device. */
    const val LABEL_ERROR: String =
      "No RIB info available. Make sure RIB app is running in foreground, then refresh."
  }

  override fun updateText(text: CompositeAppearance) {
    if (!AndroidDeviceRepositoryComponent.getInstance(project).isBridgeConnected()) {
      text.ending.addText(LABEL_NO_BRIDGE)
      return
    }

    if (!RibProjectComponent.getInstance(project).hasSelectedDevice()) {
      text.ending.addText(LABEL_NO_DEVICE)
      return
    }

    when (status) {
      RibHierarchyBrowser.Status.UNINITIALIZED -> {
        text.ending.addText(LABEL_NO_DEVICE)
      }
      RibHierarchyBrowser.Status.INITIALIZING -> {
        text.ending.addText(LABEL_WAIT)
      }
      else -> {
        val label: String = if (ribHost.name.isNotEmpty()) ribHost.name else LABEL_ERROR
        text.ending.addText(label, getDefaultTextAttributes())
      }
    }
  }

  override fun getIcon(element: PsiElement): Icon? {
    if (!RibProjectComponent.getInstance(project).hasSelectedDevice()) {
      return AllIcons.General.BalloonInformation
    }

    return when (status) {
      RibHierarchyBrowser.Status.UNINITIALIZED -> {
        AllIcons.General.BalloonInformation
      }
      RibHierarchyBrowser.Status.INITIALIZING -> {
        AllIcons.Ide.UpDown
      }
      else -> {
        AllIcons.Actions.Dump
      }
    }
  }
}
