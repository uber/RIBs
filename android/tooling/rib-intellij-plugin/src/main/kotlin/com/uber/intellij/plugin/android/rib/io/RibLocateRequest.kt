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
package com.uber.intellij.plugin.android.rib.io

import com.android.ddmlib.IDevice

/**
 * Data class representing the host of a Rib application, i.e an android device.
 *
 * @param name the name of the host
 * @param application the application model
 * @param selectedRibId the RIB ID of the RIB selected by user
 * @param selectedViewId the view ID of the view selected by user
 */
public data class RibHostWithSelection(
  val name: String,
  val application: RibApplication?,
  val selectedRibId: String,
  val selectedViewId: String,
)

/**
 * Data class representing the response of the Rib hierarchy request.
 *
 * @param host the host
 */
public data class RibHierarchyWithSelectionResponse(val host: RibHostWithSelection) :
  Response<RibHostWithSelection>()

/** Rib locate request object. */
public class EnableLocateModeRequest(device: IDevice, enabled: Boolean) :
  Request<RibHierarchyWithSelectionResponse>(
    device,
    "RIB_LOCATE",
    RibHierarchyWithSelectionResponse::class.java,
    listOf(Pair("VISIBLE", enabled)),
    TIMEOUT_MS,
    NUM_RETRIES,
  ) {
  public companion object {
    private const val TIMEOUT_MS = 1000
    private const val NUM_RETRIES = 5
  }
}
