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

import com.android.ddmlib.IDevice
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.uber.intellij.plugin.android.rib.io.EnableLocateModeRequest
import com.uber.intellij.plugin.android.rib.io.LogcatRequestProcessor
import com.uber.intellij.plugin.android.rib.io.Request
import com.uber.intellij.plugin.android.rib.io.RibHierarchyRequest
import com.uber.intellij.plugin.android.rib.io.RibHierarchyResponse
import com.uber.intellij.plugin.android.rib.io.RibHierarchyWithSelectionResponse
import com.uber.intellij.plugin.android.rib.io.RibHighlightRequest
import com.uber.intellij.plugin.android.rib.io.RibHost
import com.uber.intellij.plugin.android.rib.io.RibHostWithSelection
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Service(PROJECT)
public class RibProjectService(public val project: Project) :
  AndroidDeviceRepository.Listener, Disposable {

  public companion object {
    private const val TOOL_WINDOW_ID: String = "Ribs"
    private const val TOOL_WINDOW_TITLE: String = "Ribs"
    private const val TAB_NAME_RIBS: String = "Hierarchy"
    private const val LABEL_RIB_REFRESH: String = "Refreshing Rib Hierarchy..."
    private const val LABEL_RIB_LOCATE: String = "Waiting for RIB selection on Device..."
    private val EMPTY_MODEL: RibHierarchyBrowser.Model =
      RibHierarchyBrowser.Model(RibHost("", null), "", "")
    private val executor: Executor = Executors.newSingleThreadExecutor()
  }

  private val androidDeviceRepository = project.service<AndroidDeviceRepository>()
  private var ribPanel: RibHierarchyPanel? = null
  private var ribContent: Content? = null
  private var devices: List<IDevice> = arrayListOf()
  private var selectedDevice: IDevice? = null
  private var isRefreshing: Boolean = false
  private var isLocating: Boolean = false

  public fun attach() {
    DumbService.getInstance(project).runWhenSmart {
      ApplicationManager.getApplication().runReadAction {
        androidDeviceRepository.addListener(this)
        onModelUpdated(EMPTY_MODEL)
      }
    }
  }

  public fun refreshRibHierarchy() {
    if (isRefreshing) {
      return
    }

    if (selectedDevice == null) {
      onModelUpdated(EMPTY_MODEL)
    } else {
      val device: IDevice = selectedDevice ?: return
      isRefreshing = true
      ProgressManager.getInstance()
        .run(
          object : Task.Backgroundable(project, LABEL_RIB_REFRESH) {
            override fun run(indicator: ProgressIndicator) {
              ApplicationManager.getApplication().invokeLater {
                val request: Request<RibHierarchyResponse> = RibHierarchyRequest(device)
                val future = LogcatRequestProcessor().execute(request)
                Futures.addCallback(
                  future,
                  object : FutureCallback<RibHierarchyResponse> {
                    override fun onSuccess(result: RibHierarchyResponse?) {
                      val host: RibHost = result?.payload ?: return
                      val model = RibHierarchyBrowser.Model(host)
                      onModelUpdated(model)
                      isRefreshing = false
                    }

                    override fun onFailure(throwable: Throwable) {
                      onModelUpdated(EMPTY_MODEL)
                      isRefreshing = false
                    }
                  },
                  executor,
                )
              }
            }
          },
        )
    }
  }

  public fun highlightRib(id: UUID) {
    val device: IDevice = selectedDevice ?: return
    LogcatRequestProcessor().execute(RibHighlightRequest(device, id))
  }

  public fun highlightView(id: UUID) {
    val device: IDevice = selectedDevice ?: return
    LogcatRequestProcessor().execute(RibHighlightRequest(device, id))
  }

  public fun isLocating(): Boolean {
    return isLocating
  }

  public fun enableLocateMode() {
    if (isLocating) {
      return
    }

    val device: IDevice = selectedDevice ?: return
    ProgressManager.getInstance()
      .run(
        object : Task.Backgroundable(project, LABEL_RIB_LOCATE) {
          override fun run(indicator: ProgressIndicator) {
            ApplicationManager.getApplication().invokeLater {
              isLocating = true
              val request: Request<RibHierarchyWithSelectionResponse> =
                EnableLocateModeRequest(device, true)
              val future = LogcatRequestProcessor().execute(request)
              Futures.addCallback(
                future,
                object : FutureCallback<RibHierarchyWithSelectionResponse> {
                  override fun onSuccess(result: RibHierarchyWithSelectionResponse?) {
                    isLocating = false
                    val payload: RibHostWithSelection = result?.payload ?: return
                    val model =
                      RibHierarchyBrowser.Model(
                        RibHost(payload.name, payload.application),
                        payload.selectedRibId,
                        payload.selectedViewId,
                      )
                    onModelUpdated(model)
                  }

                  override fun onFailure(throwable: Throwable) {
                    isLocating = false
                    LogcatRequestProcessor().execute(EnableLocateModeRequest(device, false))
                  }
                },
                executor,
              )
            }
          }
        },
      )
  }

  public fun selectDevice(device: IDevice?) {
    if (selectedDevice == device) {
      return
    }
    if (device != null && !devices.contains(device)) {
      throw IllegalArgumentException("Selecting not connected device")
    }
    selectedDevice = device

    ribPanel?.onSelectedDeviceChanged(selectedDevice)

    refreshRibHierarchy()
  }

  public fun hasSelectedDevice(): Boolean {
    return selectedDevice != null
  }

  override fun onAvailableDevicesChanged(devices: List<IDevice>) {
    this.devices = devices

    ribPanel?.onAvailableDevicesChanged(devices)

    if (selectedDevice !in devices) {
      val fallbackDevice: IDevice? = if (devices.isNotEmpty()) devices[0] else null
      selectDevice(fallbackDevice)
    }
  }

  override fun dispose() {
    androidDeviceRepository.removeListener(this)
  }

  private fun onModelUpdated(model: RibHierarchyBrowser.Model) {
    ApplicationManager.getApplication().invokeLater {
      val toolWindowManager: ToolWindowManager = ToolWindowManager.getInstance(project)
      if (toolWindowManager.getToolWindow(TOOL_WINDOW_ID) == null) {
        val toolWindow: ToolWindow =
          toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.RIGHT)
        toolWindow.setIcon(IconLoader.getIcon("/icons/rib.png"))
        toolWindow.title = TOOL_WINDOW_TITLE

        ribPanel = RibHierarchyPanel(project, model)
        if (devices.isNotEmpty()) {
          ribPanel?.onAvailableDevicesChanged(devices)
        }
        ribContent = createRibContent(toolWindow)
      } else {
        ribPanel?.onModelUpdated(model)
      }
    }
  }

  private fun createRibContent(toolWindow: ToolWindow): Content {
    val content = ContentFactory.SERVICE.getInstance().createContent(ribPanel, TAB_NAME_RIBS, true)
    content.isCloseable = false
    toolWindow.contentManager.addContent(content)
    return content
  }

  public interface Listener {
    public fun onModelUpdated(model: RibHierarchyBrowser.Model)
  }
}
