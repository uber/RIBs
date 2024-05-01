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
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.openapi.project.Project
import com.uber.intellij.plugin.android.rib.RibHierarchyUtils

/** Tree structure used by Rib hierarchy */
public class RibHierarchyTreeStructure(
  private val project: Project,
  descriptor: HierarchyNodeDescriptor,
) : HierarchyTreeStructure(project, descriptor) {

  init {
    setBaseElement(descriptor)
  }

  override fun buildChildren(descriptor: HierarchyNodeDescriptor): Array<Any> {
    val descriptors: ArrayList<HierarchyNodeDescriptor> = ArrayList(1)
    when (descriptor) {
      is RibHierarchyRootNodeDescriptor -> {
        descriptor.ribHost.application?.let {
          descriptors.add(
            RibHierarchyApplicationDescriptor(
              myProject,
              descriptor,
              RibHierarchyUtils.getPsiClass(project, descriptor.ribHost.name),
              it,
            ),
          )
        }
      }
      is RibHierarchyApplicationDescriptor -> {
        descriptor.ribApplication.activities.forEach { activity ->
          descriptors.add(
            RibHierarchyActivityDescriptor(
              myProject,
              descriptor,
              RibHierarchyUtils.getPsiClass(project, activity.name),
              activity,
            ),
          )
        }
      }
      is RibHierarchyActivityDescriptor -> {
        descriptors.add(
          RibHierarchyNodeDescriptor(
            myProject,
            descriptor,
            RibHierarchyUtils.getPsiClass(project, descriptor.ribActivity.name),
            descriptor.ribActivity.rootRib,
          ),
        )
      }
      is RibHierarchyNodeDescriptor -> {
        descriptor.ribNode.children.forEach { childRibNode ->
          descriptors.add(
            RibHierarchyNodeDescriptor(
              myProject,
              descriptor,
              RibHierarchyUtils.getPsiClass(project, childRibNode.name),
              childRibNode,
            ),
          )
        }
      }
      is RibViewRootNodeDescriptor -> {
        descriptor.ribView?.children?.forEach { view ->
          descriptors.add(
            RibViewNodeDescriptor(
              myProject,
              RibHierarchyUtils.getPsiClass(project, view.id),
              descriptor.ribNode,
              view,
            ),
          )
        }
      }
      is RibViewNodeDescriptor -> {
        descriptor.ribView?.children?.forEach { view ->
          descriptors.add(
            RibViewNodeDescriptor(
              myProject,
              RibHierarchyUtils.getPsiClass(project, view.id),
              descriptor.ribNode,
              view,
            ),
          )
        }
      }
    }
    return descriptors.toTypedArray()
  }
}
