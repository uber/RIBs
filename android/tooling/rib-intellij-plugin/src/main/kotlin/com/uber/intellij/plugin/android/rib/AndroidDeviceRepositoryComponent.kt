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
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/** IntelliJ Project component responsible for exposing connected Android devices. */
class AndroidDeviceRepositoryComponent(val project: Project) :
  ProjectComponent, AndroidDebugBridge.IDeviceChangeListener {

  companion object {
    private const val timeOutMs: Long = 5
    private const val sleepTimeMs: Long = 1

    fun getInstance(project: Project): AndroidDeviceRepositoryComponent {
      return project.getComponent(AndroidDeviceRepositoryComponent::class.java)
    }

    @SuppressWarnings("TooGenericExceptionCaught", "SwallowedException")
    fun getBridge(project: Project): AndroidDebugBridge? {
      if (isInitialized()) {
        return AndroidDebugBridge.getBridge()
      }

      var adb: AndroidDebugBridge? = null
      try {
        // Initialize bridge
        val adbPath = getAdbPath(project)
        AndroidDebugBridge.initIfNeeded(false)
        adb = AndroidDebugBridge.createBridge(adbPath, false)

        // Ensure device list is initialized
        var timeOutMs = TimeUnit.SECONDS.toMillis(timeOutMs)
        val sleepTimeMs = TimeUnit.SECONDS.toMillis(sleepTimeMs)
        while (!adb.hasInitialDeviceList() && timeOutMs > 0) {
          try {
            Thread.sleep(sleepTimeMs)
          } catch (e: InterruptedException) {
            throw TimeoutException()
          }

          timeOutMs -= sleepTimeMs
        }
        if (timeOutMs <= 0 && !adb.hasInitialDeviceList()) {
          throw TimeoutException()
        }
      } catch (e: Exception) {
        // Android Debug Bridge could not be obtained or initialized.
      }
      return adb
    }

    @SuppressWarnings("TooGenericExceptionCaught", "SwallowedException")
    private fun getAdbPath(project: Project): String? {
      return try {
        CommandLineUtils.which(project, "adb")
      } catch (e: Exception) {
        // Android Debug Bridge location could not be obtained.
        null
      }
    }

    private fun isInitialized(): Boolean {
      return AndroidDebugBridge.getBridge() != null &&
        AndroidDebugBridge.getBridge().isConnected &&
        AndroidDebugBridge.getBridge().hasInitialDeviceList()
    }
  }

  private val devices: ArrayList<IDevice> = arrayListOf()
  private val listeners: ArrayList<Listener> = arrayListOf()

  override fun projectOpened() {
    super.projectOpened()

    getBridge(project)?.devices?.forEach { devices.add(it) }

    AndroidDebugBridge.addDeviceChangeListener(this)
  }

  override fun projectClosed() {
    super.projectClosed()

    AndroidDebugBridge.removeDeviceChangeListener(this)
    devices.clear()
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
  fun addListener(listener: Listener) {
    listeners.add(listener)
    if (devices.size > 0) {
      listener.onAvailableDevicesChanged(devices)
    }
  }

  @Synchronized
  fun removeListener(listener: Listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener)
    }
  }

  @Synchronized
  private fun broadcastChanges() {
    listeners.forEach { it.onAvailableDevicesChanged(devices) }
  }

  fun getDevices(): List<IDevice> {
    return devices
  }

  fun isBridgeConnected(): Boolean {
    return getBridge(project)?.isConnected == true
  }

  interface Listener {

    fun onAvailableDevicesChanged(devices: List<IDevice>)
  }
}
