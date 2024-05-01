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

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.project.Project
import org.jetbrains.android.sdk.AndroidSdkUtils

/** IntelliJ Project component responsible for exposing connected Android devices. */
@Service(PROJECT)
public class AndroidDeviceRepository(public val project: Project) :
  AndroidDebugBridge.IDeviceChangeListener, Disposable {

  private val devices: ArrayList<IDevice> = arrayListOf()
  private val listeners: ArrayList<Listener> = arrayListOf()

  init {
    ApplicationManager.getApplication().invokeLater {
      AndroidSdkUtils.getDebugBridge(project)?.devices?.forEach { devices.add(it) }
      AndroidDebugBridge.addDeviceChangeListener(this)
    }
  }

  @Suppress("EmptyFunctionBlock") override fun deviceChanged(device: IDevice, changeMask: Int) {}

  override fun deviceConnected(device: IDevice) {
    if (!devices.contains(device)) {
      devices.add(device)
      broadcastChanges()
    } else {
      throw IllegalArgumentException("Adding a device that already exists")
    }
  }

  override fun deviceDisconnected(device: IDevice) {
    if (devices.contains(device)) {
      devices.remove(device)
      broadcastChanges()
    } else {
      throw IllegalArgumentException("Removing a device that does not exist")
    }
  }

  @Synchronized
  public fun addListener(listener: Listener) {
    listeners.add(listener)
    if (devices.size > 0) {
      listener.onAvailableDevicesChanged(devices)
    }
  }

  @Synchronized
  public fun removeListener(listener: Listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener)
    }
  }

  @Synchronized
  private fun broadcastChanges() {
    listeners.forEach { it.onAvailableDevicesChanged(devices) }
  }

  public fun isBridgeConnected(): Boolean {
    val debugBridge = AndroidDebugBridge.getBridge()
    return debugBridge != null && with(debugBridge) { isConnected && hasInitialDeviceList() }
  }

  override fun dispose() {
    AndroidDebugBridge.removeDeviceChangeListener(this)
    devices.clear()
  }

  public interface Listener {

    public fun onAvailableDevicesChanged(devices: List<IDevice>)
  }
}
