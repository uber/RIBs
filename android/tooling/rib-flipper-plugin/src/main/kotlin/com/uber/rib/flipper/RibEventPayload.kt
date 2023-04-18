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
package com.uber.rib.flipper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import com.facebook.flipper.core.FlipperObject
import com.uber.rib.core.RibEventType
import com.uber.rib.core.Router
import com.uber.rib.core.ViewRouter

/** Payload used by Rib Flipper Plugin. */
internal class RibEventPayload(
  private val sessionId: String,
  private val eventType: RibEventType,
  private val routerId: String,
  private val router: Router<*>,
  private val parentRouterId: String,
  private val parentRouter: Router<*>?,
) {

  companion object {
    const val ROUTER_NAME_PREFIX: String = "Router"
    const val EVENT_PARAMETER_ID: String = "id"
    const val EVENT_PARAMETER_HOST_CLASSNAME: String = "hostClassName"
    const val EVENT_PARAMETER_ROUTER_CLASSNAME: String = "routerClassName"
    const val EVENT_PARAMETER_SESSION_ID: String = "sessionId"
    const val EVENT_PARAMETER_ROUTER: String = "router"
    const val EVENT_PARAMETER_PARENT: String = "parent"
    const val EVENT_PARAMETER_NAME: String = "name"
    const val EVENT_PARAMETER_HAS_VIEW: String = "hasView"
  }

  val eventName: String
    get() = eventType.toString()

  val flipperPayload: FlipperObject
    get() =
      FlipperObject.Builder()
        .put(EVENT_PARAMETER_SESSION_ID, sessionId)
        .put(EVENT_PARAMETER_ROUTER, RibEventRouterPayload(routerId, router).flipperPayload)
        .put(
          EVENT_PARAMETER_PARENT,
          RibEventRouterPayload(parentRouterId, parentRouter).flipperPayload,
        )
        .build()

  internal class RibEventRouterPayload
  constructor(private val id: String, private val router: Router<*>?) {

    val flipperPayload: FlipperObject
      get() {
        val name =
          if (router is Router<*>) {
            router.javaClass.simpleName.replace(ROUTER_NAME_PREFIX, "")
          } else {
            ""
          }
        val routerClassName = if (router is Router<*>) router.javaClass.simpleName else ""
        val hasView = router is ViewRouter<*, *>
        val activityClassName = if (router is ViewRouter<*, *>) getActivityClassName(router) else ""
        return FlipperObject.Builder()
          .put(EVENT_PARAMETER_ID, id)
          .put(EVENT_PARAMETER_NAME, name)
          .put(EVENT_PARAMETER_ROUTER_CLASSNAME, routerClassName)
          .put(EVENT_PARAMETER_HAS_VIEW, hasView)
          .put(EVENT_PARAMETER_HOST_CLASSNAME, activityClassName)
          .build()
      }

    private fun getActivityClassName(router: ViewRouter<*, *>): String {
      val view: android.view.View? = router.view
      if (view != null) {
        var context: android.content.Context? = view.getContext()
        while (context is ContextWrapper) {
          if (context is Activity) {
            return context.javaClass.getName()
          }
          context = context.getBaseContext()
        }
      }
      return ""
    }
  }
}
