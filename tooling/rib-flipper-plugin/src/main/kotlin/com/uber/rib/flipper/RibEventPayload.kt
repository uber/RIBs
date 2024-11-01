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
  private val routerInfo: RouterInfo,
  private val parentRouterInfo: RouterInfo,
) {

  companion object {
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

  fun toFlipperPayload(): FlipperObject {
    return FlipperObject.Builder()
      .put(EVENT_PARAMETER_SESSION_ID, sessionId)
      .put(EVENT_PARAMETER_ROUTER, routerInfo.toFlipperPayload())
      .put(EVENT_PARAMETER_PARENT, parentRouterInfo.toFlipperPayload())
      .build()
  }

  internal class RouterInfo(
    val id: String,
    val name: String,
    val className: String,
    val hasView: Boolean,
    val activityClassName: String,
  ) {
    companion object {
      private const val ROUTER_NAME_PREFIX: String = "Router"

      fun fromRouter(router: Router<*>?, routerId: String) =
        RouterInfo(
          id = routerId,
          name = router?.javaClass?.simpleName?.replace(ROUTER_NAME_PREFIX, "") ?: "",
          className = router?.javaClass?.simpleName ?: "",
          hasView = router is ViewRouter<*, *>,
          activityClassName = if (router is ViewRouter<*, *>) getActivityClassName(router) else "",
        )

      private fun getActivityClassName(router: ViewRouter<*, *>): String {
        val view: View = router.view
        var context: Context? = view.context
        while (context is ContextWrapper) {
          if (context is Activity) {
            return context.javaClass.name
          }
          context = context.baseContext
        }
        return ""
      }
    }

    fun toFlipperPayload(): FlipperObject {
      return FlipperObject.Builder()
        .put(EVENT_PARAMETER_ID, id)
        .put(EVENT_PARAMETER_NAME, name)
        .put(EVENT_PARAMETER_ROUTER_CLASSNAME, className)
        .put(EVENT_PARAMETER_HAS_VIEW, hasView)
        .put(EVENT_PARAMETER_HOST_CLASSNAME, activityClassName)
        .build()
    }
  }
}
