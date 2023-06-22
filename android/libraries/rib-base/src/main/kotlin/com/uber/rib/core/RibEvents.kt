/*
 * Copyright (C) 2017. Uber Technologies
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
package com.uber.rib.core

import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.rx2.asObservable

public object RibEvents {

  private val mutableRouterEvents =
    MutableSharedFlow<RibRouterEvent>(0, 1, BufferOverflow.DROP_OLDEST)
  private val mutableRibDurationEvents =
    MutableSharedFlow<RibActionInfo>(0, 1, BufferOverflow.DROP_OLDEST)

  @JvmStatic
  public val routerEvents: Observable<RibRouterEvent> = mutableRouterEvents.asObservable()

  @JvmStatic
  public val ribActionEvents: Observable<RibActionInfo> = mutableRibDurationEvents.asObservable()

  private var areRibActionEmissionsAllowed = false

  /**
   * To be called before start observing/collecting on [ribActionEvents] (usually at your earliest
   * application point)
   */
  @JvmStatic
  public fun enableRibActionEmissions() {
    this.areRibActionEmissionsAllowed = true
  }

  /** Only to be used within test * */
  @VisibleForTesting
  internal fun disableRibActionEmissions() {
    this.areRibActionEmissionsAllowed = false
  }

  /**
   * @param eventType [RibEventType]
   * @param child [Router]
   * @param parent [Router] and null for the root ribs that are directly attached to
   *   RibActivity/Fragment
   */
  @JvmStatic
  public fun emitRouterEvent(eventType: RibEventType, child: Router<*>, parent: Router<*>?) {
    mutableRouterEvents.tryEmit(RibRouterEvent(eventType, child, parent))
  }

  /**
   * Calls related RIB action (e.g. didBecomeActive) and emits emission of ATTACHED/DETACHED events
   * for each RIB component.
   *
   * @param ribAction The related RIB action type. e.g. didBecomeActive, willLoad, etc
   * @param RibEventEmitter Related RIB component
   * @param RibEventEmitterType The RIB component type (e.g. Interactor, Router, Presenter, Worker)
   * @param ribEventType RIB event type (e.g. ATTACH/DETACH)
   */
  internal inline fun triggerRibActionAndEmitEvents(
    RibEventEmitter: RibEventEmitter,
    RibEventEmitterType: RibEventEmitterType,
    ribEventType: RibEventType,
    ribAction: () -> Unit,
  ) {
    emitRibEventActionIfNeeded(
      RibEventEmitter,
      RibEventEmitterType,
      ribEventType,
      RibActionState.STARTED,
    )
    ribAction()
    emitRibEventActionIfNeeded(
      RibEventEmitter,
      RibEventEmitterType,
      ribEventType,
      RibActionState.COMPLETED,
    )
  }

  /**
   * Emits emission of ATTACHED/DETACHED events for each RIB component.
   *
   * @param RibEventEmitterType The RIB component type (e.g. Interactor, Router, Presenter)
   * @param ribEventType RIB event type (e.g. ATTACH/DETACH)
   * @param ribActionState: RibActionType,
   */
  private fun emitRibEventActionIfNeeded(
    RibEventEmitter: RibEventEmitter,
    RibEventEmitterType: RibEventEmitterType,
    ribEventType: RibEventType,
    ribActionState: RibActionState,
  ) {
    if (!areRibActionEmissionsAllowed) {
      // Unless specified explicitly via [RibEvents.allowRibActionEmissions] there is no need
      // to create unnecessary objects if no one is observing/collecting RibAction events
      return
    }

    val ribActionInfo =
      RibActionInfo(
        RibEventEmitter.javaClass.name,
        RibEventEmitterType,
        ribEventType,
        ribActionState,
        Thread.currentThread().name,
      )
    mutableRibDurationEvents.tryEmit(ribActionInfo)
  }
}

/** Holds relevant RIB event information */
public data class RibActionInfo(
  /** Related RIB class name */
  val ribEventEmitterName: String,

  /** The current RIB event type being bound (e.g. Interactor/Presenter/Router) */
  val RibEventEmitterType: RibEventEmitterType,

  /** Represents the RIB event type, e.g. ATTACHED/DETACHED */
  val ribEventType: RibEventType,

  /** RIB Action state (e.g. event to be called before/after didBecomeActive, willLoad, etc) */
  val ribActionState: RibActionState,

  /** Original caller thread where the RIB action happens */
  val originalCallerThreadName: String,
)

/**
 * Contract for all related Rib Components e.g. Interactor, Presenter, Router, Workers where will be
 * emitting via [ribActionEvents]
 */
public interface RibEventEmitter

public enum class RibEventEmitterType {
  ROUTER,
  PRESENTER,
  INTERACTOR,
  DEPRECATED_WORKER,
}

/** Represents status for each RibAction */
public enum class RibActionState {
  STARTED,
  COMPLETED,
}
