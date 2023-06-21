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

  /**
   * @param eventType [RibEventType]
   * @param child [Router]
   * @param parent [Router] and null for the root ribs that are directly attached to
   *   RibActivity/Fragment
   */
  public fun emitRouterEvent(eventType: RibEventType, child: Router<*>, parent: Router<*>?) {
    mutableRouterEvents.tryEmit(RibRouterEvent(eventType, child, parent))
  }

  /**
   * Calls related RIB action (e.g. didBecomeActive) and emits emission of ATTACHED/DETACHED events
   * for each RIB component.
   *
   * @param ribAction The related RIB action type. e.g. didBecomeActive, willLoad, etc
   * @param ribCallerClassType Related RIB component class type
   * @param ribComponentType The RIB component type (e.g. Interactor, Router, Presenter, Worker)
   * @param ribEventType RIB event type (e.g. ATTACH/DETACH)
   */
  internal fun <T : Any> triggerRibActionAndEmitEvents(
    ribCallerClassType: T,
    ribComponentType: RibComponentType,
    ribEventType: RibEventType,
    ribAction: () -> Unit,
  ) {
    val ribClassName = ribCallerClassType.asQualifiedName()
    ribClassName?.emitRibEventAction(ribComponentType, ribEventType, RibActionState.STARTED)
    ribAction()
    ribClassName?.emitRibEventAction(ribComponentType, ribEventType, RibActionState.COMPLETED)
  }

  private fun Any.asQualifiedName(): String? = javaClass.kotlin.qualifiedName

  /**
   * Emits emission of ATTACHED/DETACHED events for each RIB component.
   *
   * @param ribComponentType The RIB component type (e.g. Interactor, Router, Presenter)
   * @param ribEventType RIB event type (e.g. ATTACH/DETACH)
   * @param ribActionState: RibActionType,
   */
  private fun String.emitRibEventAction(
    ribComponentType: RibComponentType,
    ribEventType: RibEventType,
    ribActionState: RibActionState,
  ) {
    val ribActionInfo =
      RibActionInfo(
        this,
        ribComponentType,
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
  val className: String,

  /** The current RIB event type being bound (e.g. Interactor/Presenter/Router) */
  val ribComponentType: RibComponentType,

  /** Represents the RIB event type, e.g. ATTACHED/DETACHED */
  val ribEventType: RibEventType,

  /** RIB Action state (e.g. event to be called before/after didBecomeActive, willLoad, etc) */
  val ribActionState: RibActionState,

  /** Original caller thread where the RIB action happens */
  val originalCallerThreadName: String,
)

/** Represents status for each RibAction */
public enum class RibActionState {
  STARTED,
  COMPLETED,
}
