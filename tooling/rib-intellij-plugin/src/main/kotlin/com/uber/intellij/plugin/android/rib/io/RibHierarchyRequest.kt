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

/** Data class representing the host of a Rib application, i.e an android device. */
public data class RibHost(val name: String, val application: RibApplication?)

/** Data class representing a Rib application. */
public data class RibApplication(val name: String, val activities: List<RibActivity>)

/** Data class representing a Rib activity. */
public data class RibActivity(val name: String, val rootRib: RibNode)

/**
 * Data class representing a Rib node.
 *
 * @param id the id of the rib node
 * @param name the name of the rib node
 * @param children the list of children for this node
 * @param view the view for this rib node
 */
public data class RibNode(
  val id: String,
  val name: String,
  val children: List<RibNode>,
  val view: RibView,
)

/**
 * Data class representing a Rib view.
 *
 * @param id the id of the rib view
 * @param name the name of the rib view
 * @param viewId the view id for thie view
 * @param layoutId the name of the layout this view was inflated from
 * @param children the list of children for this view
 */
public data class RibView(
  val id: String,
  val name: String,
  val viewId: String,
  val layoutId: String,
  val children: List<RibView>,
)

/** Data class representing the response of the Rib hierarchy request. */
public data class RibHierarchyResponse(val host: RibHost) : Response<RibHost>()

/** Data class representing the request for a Rib hierarchy. */
public class RibHierarchyRequest(device: IDevice) :
  Request<RibHierarchyResponse>(device, "RIB_HIERARCHY", RibHierarchyResponse::class.java)
