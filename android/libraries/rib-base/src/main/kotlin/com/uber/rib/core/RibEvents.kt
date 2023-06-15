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
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.rx2.asObservable

public object RibEvents {

  private val mutableRouterEvents =
    MutableSharedFlow<RibRouterEvent>(0, 1, BufferOverflow.DROP_OLDEST)
  private val mutableRibDurationEvents =
    MutableSharedFlow<RibEventDurationData>(0, 1, BufferOverflow.DROP_OLDEST)

  @JvmStatic
  public val routerEvents: Observable<RibRouterEvent>
    get() = mutableRouterEvents.asObservable()

  @JvmStatic
  public val ribDurationEvents: Observable<RibEventDurationData>
    get() = mutableRibDurationEvents.asObservable()

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
   * Emits emission of ATTACHED/DETACHED events for each RIB component.
   *
   * @param ribClass Class names for custom RIB implementations (e.g. LoggedInInteractor,
   *   UiRibWorker, etc)
   * @param ribComponentType The RIB component type (e.g. Interactor, Router, Presenter)
   * @param ribEventType RIB event type (e.g. ATTACH/DETACH)
   * @param totalBindingDurationMilli Total duration (in ms) of each ATTACH/DETACH events
   */
  internal fun emitRibEventDuration(
    ribClass: KClass<*>,
    ribComponentType: RibComponentType,
    ribEventType: RibEventType,
    totalBindingDurationMilli: Long,
  ) {
    val ribClassName = ribClass.qualifiedName

    // There's no point to emit emission if we don't know which RIB component name was triggered
    ribClassName?.let {
      val ribEventData =
        RibEventDurationData(
          it,
          ribComponentType,
          ribEventType,
          Thread.currentThread().name,
          totalBindingDurationMilli,
        )
      mutableRibDurationEvents.tryEmit(ribEventData)
    }
  }
}

/** Holds relevant RIB event information */
public data class RibEventDurationData(
  /** Related RIB class name */
  val className: String,

  /** The current RIB event type being bound (e.g. Interactor/Presenter/Router) */
  val ribComponentType: RibComponentType,

  /** RIB component event type ATTACHED/DETACHED */
  val ribEventType: RibEventType,

  /** Reports the current thread name where Rib Event happen (should mainly be main thread) */
  val threadName: String,

  /** Total binding duration in milliseconds of Worker.onStart/onStop */
  val totalBindingDurationMilli: Long,
)
