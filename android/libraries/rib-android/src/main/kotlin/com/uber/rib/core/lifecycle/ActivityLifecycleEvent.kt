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
package com.uber.rib.core.lifecycle

import android.os.Bundle

/**
 * Lifecycle events that can be emitted by Activities.
 *
 * ### Ordering semantics
 *
 * This class implements [Comparable], but it does **not** override `equals`, so even though the
 * following holds:
 * ```
 * val a = createOnCreateEvent(null)
 * val b = createOnCreateEvent(Bundle())
 * assertThat(a <= b).isTrue()
 * assertThat(b <= a).isTrue()
 * ```
 *
 * The equality does not hold:
 * ```
 * assertThat(a == b).isFalse()
 * ```
 *
 * This happens because events of type [CREATE][ActivityLifecycleEvent.Type.CREATE] hold a [Bundle],
 * and even though any two `CREATE` events are equal in ordering ([compareTo]), they are never equal
 * in [equals] comparison.
 *
 * In mathematical terms, the activity events set form a
 * [total preorder](https://en.wikipedia.org/wiki/Weak_ordering#Total_preorders), but *not* a
 * [total order](https://en.wikipedia.org/wiki/Total_order): it is reflexive, transitive, strongly
 * connected, but **not** antisymmetric.
 */
open class ActivityLifecycleEvent
private constructor(
  /** @return this event's type. */
  override val type: Type,
) : ActivityEvent, Comparable<ActivityLifecycleEvent> {

  /** Types of activity events that can occur. */
  enum class Type : ActivityEvent.BaseType {
    CREATE,
    START,
    RESUME,
    USER_LEAVING,
    PAUSE,
    STOP,
    DESTROY,
  }

  override fun compareTo(other: ActivityLifecycleEvent): Int = type.compareTo(other.type)

  /**
   * An [ActivityLifecycleEvent] that encapsulates information from
   * [Activity.onCreate][android.app.Activity.onCreate].
   */
  open class Create(
    /** @return this event's savedInstanceState data. */
    open val savedInstanceState: Bundle?,
  ) : ActivityLifecycleEvent(Type.CREATE)

  companion object {
    private val START_EVENT = ActivityLifecycleEvent(Type.START)
    private val RESUME_EVENT = ActivityLifecycleEvent(Type.RESUME)
    private val USER_LEAVING_EVENT = ActivityLifecycleEvent(Type.USER_LEAVING)
    private val PAUSE_EVENT = ActivityLifecycleEvent(Type.PAUSE)
    private val STOP_EVENT = ActivityLifecycleEvent(Type.STOP)
    private val DESTROY_EVENT = ActivityLifecycleEvent(Type.DESTROY)

    /**
     * Creates an event for onCreate.
     *
     * @param stateData the instate bundle.
     * @return the created ActivityEvent.
     */
    @JvmStatic
    fun createOnCreateEvent(stateData: Bundle?): Create {
      return Create(stateData)
    }

    /**
     * Creates an activity event for a given type.
     *
     * @param type The type of event to get.
     * @return The corresponding ActivityEvent.
     */
    @JvmStatic
    fun create(type: Type): ActivityLifecycleEvent {
      return when (type) {
        Type.START -> START_EVENT
        Type.RESUME -> RESUME_EVENT
        Type.USER_LEAVING -> USER_LEAVING_EVENT
        Type.PAUSE -> PAUSE_EVENT
        Type.STOP -> STOP_EVENT
        Type.DESTROY -> DESTROY_EVENT
        else ->
          throw IllegalArgumentException(
            "Use the createOn${type.name.lowercase().replaceFirstChar(Char::titlecase)}Event() method for this type!",
          )
      }
    }
  }
}
