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

import android.util.Log
import android.view.View
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.facebook.flipper.core.FlipperResponder
import com.uber.rib.core.RibDebugOverlay
import com.uber.rib.core.RibEvent
import com.uber.rib.core.RibEvents
import com.uber.rib.core.Router
import com.uber.rib.core.ViewRouter
import com.uber.rib.flipper.RibEventPayload.Companion.EVENT_PARAMETER_ID
import com.uber.rib.flipper.RibTreeMessageType.HIDE_HIGHLIGHT
import com.uber.rib.flipper.RibTreeMessageType.SHOW_HIGHLIGHT
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import java.lang.ref.WeakReference
import java.util.HashMap
import java.util.UUID
import java.util.WeakHashMap
import kotlin.jvm.Synchronized

/** Flipper debug tool plugin to help with RIBs developement. */
class RibTreePlugin : FlipperPlugin {
  private var connection: FlipperConnection? = null
  private var disposable: Disposable? = null
  private val events: ReplaySubject<RibEventPayload> = ReplaySubject.create(EVENTS_CAPACITY)
  private val sessionId: String = UUID.randomUUID().toString()
  private val idsToOverlay: MutableMap<String, WeakReference<RibDebugOverlay>> =
    HashMap<String, WeakReference<RibDebugOverlay>>()
  private val routersToId: WeakHashMap<Router<*>, String> = WeakHashMap<Router<*>, String>()

  companion object {
    private val TAG: String = RibTreePlugin::class.java.simpleName
    private const val EVENTS_CAPACITY = 1000
  }

  init {
    // Start listening to rib events right away, since flipper client might connect only later on
    RibEvents.getInstance()
      .events
      .filter { e: RibEvent -> e.parentRouter != null }
      .map { e: RibEvent ->
        val router: Router<*> = e.router
        val routerId = createRouterIdIfNeeded(router)
        val parentRouter: Router<*>? = e.parentRouter
        val parentRouterId = createRouterIdIfNeeded(parentRouter)
        RibEventPayload(sessionId, e.eventType, routerId, router, parentRouterId, parentRouter)
      }
      .subscribe(events)
  }

  override fun getId(): String {
    return "ribtree"
  }

  override fun onConnect(connection: FlipperConnection) {
    android.util.Log.d("RibTreeFlipperPlugin", "onConnect()")
    this.connection = connection
    disposable =
      events.subscribe { e: RibEventPayload ->
        this.connection?.send(e.eventName, e.flipperPayload)
      }
    connection.receive(SHOW_HIGHLIGHT.toString()) { params: FlipperObject, _: FlipperResponder? ->
      val id: String = params.getString(EVENT_PARAMETER_ID)
      val router: Router<*>? = getRouterById(id)
      if (router is ViewRouter<*, *>) {
        val view: android.view.View = router.view
        val overlay = RibDebugOverlay()
        view.getOverlay().add(overlay)
        view.invalidate()
        idsToOverlay.put(id, WeakReference<RibDebugOverlay>(overlay))
      }
    }
    connection.receive(HIDE_HIGHLIGHT.toString()) { params: FlipperObject, _: FlipperResponder? ->
      val id: String = params.getString(EVENT_PARAMETER_ID)
      val router: Router<*>? = getRouterById(id)
      if (router is ViewRouter<*, *>) {
        val view: android.view.View = router.view
        val overlayRef: WeakReference<RibDebugOverlay> = idsToOverlay[id] ?: return@receive
        idsToOverlay.remove(id)
        val overlay: RibDebugOverlay = overlayRef.get() ?: return@receive
        view.getOverlay().remove(overlay)
        view.invalidate()
      }
    }
  }

  override fun onDisconnect() {
    android.util.Log.d(TAG, "onDisconnect()")
    disposable?.dispose()
    disposable = null
    connection = null
  }

  override fun runInBackground(): Boolean {
    return true
  }

  @Synchronized
  private fun getRouterById(id: String): Router<*>? {
    for ((key, value) in routersToId.entries) {
      if (value.compareTo(id, ignoreCase = true) == 0) {
        return key
      }
    }
    return null
  }

  @Synchronized
  private fun createRouterIdIfNeeded(router: Router<*>?): String {
    var id: String? = routersToId[router]
    if (id == null) {
      id = UUID.randomUUID().toString()
      routersToId[router] = id
    }
    return id
  }
}
