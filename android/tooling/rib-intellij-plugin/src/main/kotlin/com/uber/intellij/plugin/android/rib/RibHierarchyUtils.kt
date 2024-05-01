/*
 * Copyright (C) 2021. Uber Technologies
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

/*
 * Copyright (c) 2018-2019 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.awt.RelativePoint
import com.uber.intellij.plugin.android.rib.io.RibNode
import com.uber.intellij.plugin.android.rib.io.RibView
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

/** Utility class used by the Rib hierarchy browser component. */
@SuppressWarnings("TooManyFunctions")
public class RibHierarchyUtils {

  private constructor()

  public companion object {

    /** Constant used to represent empty UUID */
    public val EMPTY_UUID: UUID = UUID(0, 0)

    /** Time for balloon to fade out */
    private const val BALLOON_FADE_OUT_TIME: Long = 3000

    /** Name of layout folder */
    private const val LAYOUT_FOLDER_NAME: String = "layout"

    /** Build root element, used when class is not available. */
    public fun buildRootElement(project: Project): PsiClass {
      val psiClass: PsiClass? =
        JavaPsiFacade.getInstance(project)
          .findClass(Object::class.java.name, GlobalSearchScope.allScope(project))
      checkNotNull(psiClass)
      return psiClass!!
    }

    /** Return the psiClass corresponding to the given class name. */
    public fun getPsiClass(project: Project, name: String): PsiClass {
      val psiClass: PsiClass? =
        JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.allScope(project))
      return psiClass ?: buildRootElement(project)
    }

    /** Check if the element supplied is a root element. */
    public fun isRootElement(element: PsiElement?): Boolean {
      return element is PsiClass && element.qualifiedName == Object::class.java.name
    }

    /** Format fully qualified class name. */
    public fun formatQualifiedName(qualifiedName: String): String {
      val index: Int = qualifiedName.lastIndexOf(".")
      return if (index > 0) qualifiedName.substring(0, index) else qualifiedName
    }

    /** Format fully qualified class name. */
    public fun formatSimpleName(qualifiedName: String): String {
      val index: Int = qualifiedName.lastIndexOf(".")
      return if (index > 0) qualifiedName.substring(index + 1) else qualifiedName
    }

    /** Find node with the given ID in node hierarchy */
    @SuppressWarnings("ReturnCount")
    public fun findRibNodeRecursive(ribNode: RibNode?, id: UUID): RibNode? {
      if (ribNode == null) {
        return null
      }
      if (ribNode.id == id.toString()) {
        return ribNode
      }
      for (element in ribNode.children) {
        val node: RibNode? = findRibNodeRecursive(element, id)
        if (node != null) {
          return node
        }
      }
      return null
    }

    /** Find view with the given ID in view hierarchy */
    @SuppressWarnings("ReturnCount")
    public fun findRibViewRecursive(ribView: RibView?, id: UUID): RibView? {
      if (ribView == null) {
        return null
      }
      if (ribView.id == id.toString()) {
        return ribView
      }
      for (childView in ribView.children) {
        val view: RibView? = findRibViewRecursive(childView, id)
        if (view != null) {
          return view
        }
      }
      return null
    }

    /** Get a view tag value suffix */
    public fun getTagValueSuffix(value: String?): String? {
      if (value == null) {
        return null
      }
      val index = value.indexOf("/")
      return if (index > 0) value.substring(index + 1) else value
    }

    /** Get virtual file from path */
    @SuppressWarnings("MagicNumber")
    public fun getVirtualFile(filePath: String): VirtualFile? {
      val actualPath: Path = Paths.get(filePath)
      val pathFile: File = actualPath.toFile()
      return LocalFileSystem.getInstance().findFileByIoFile(pathFile)
    }

    /** Returns whether virtual file belongs to project and appears to be a layout file */
    public fun isProjectLayoutFile(project: Project, file: VirtualFile): Boolean {
      return ProjectRootManager.getInstance(project).fileIndex.isInContent(file) &&
        file.fileType is XmlFileType &&
        file.path.contains("/$LAYOUT_FOLDER_NAME/")
    }

    /** Display popup balloon. */
    public fun displayPopup(
      message: String,
      location: RelativePoint,
      type: MessageType = MessageType.WARNING,
    ) {
      JBPopupFactory.getInstance()
        .createHtmlTextBalloonBuilder(message, type, null)
        .setFadeoutTime(BALLOON_FADE_OUT_TIME)
        .createBalloon()
        .show(location, Balloon.Position.above)
    }

    /** Display notification bubble. */
    public fun log(message: String) {
      Notifications.Bus.notify(Notification("Rib", "Rib", message, NotificationType.INFORMATION))
    }
  }
}
