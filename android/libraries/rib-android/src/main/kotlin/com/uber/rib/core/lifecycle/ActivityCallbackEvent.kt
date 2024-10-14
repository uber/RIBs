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

import android.content.Intent
import android.os.Bundle

/** Callback events that can be emitted by Activities. */
public open class ActivityCallbackEvent
private constructor(
  /** @return this event's type. */
  override val type: Type,
) : ActivityEvent {

  /** Types of activity events that can occur. */
  public enum class Type : ActivityEvent.BaseType {
    LOW_MEMORY,
    ACTIVITY_RESULT,
    SAVE_INSTANCE_STATE,
    TRIM_MEMORY,
    PICTURE_IN_PICTURE_MODE,
    NEW_INTENT,
    WINDOW_FOCUS,
  }

  /** An [ActivityCallbackEvent] that represents [Activity.onNewIntent] event */
  public open class NewIntent(public open val intent: Intent) :
    ActivityCallbackEvent(Type.NEW_INTENT)

  public open class PictureInPictureMode(public open val isInPictureInPictureMode: Boolean) :
    ActivityCallbackEvent(Type.PICTURE_IN_PICTURE_MODE)

  /** An [ActivityCallbackEvent] that represents [Activity.onWindowFocusChanged] event */
  public open class WindowFocus(public open val hasFocus: Boolean) :
    ActivityCallbackEvent(Type.WINDOW_FOCUS)

  /** An [ActivityCallbackEvent] that represents [Activity.onTrimMemory] event */
  public open class TrimMemory internal constructor(public open val trimMemoryType: Int) :
    ActivityCallbackEvent(Type.TRIM_MEMORY)

  /** An [ActivityCallbackEvent] that encapsulates information from [Activity.onActivityResult]. */
  public open class ActivityResult(
    /** @return this event's activity result data intent. */
    public open val data: Intent?,
    /** @return this event's request code. */
    public open val requestCode: Int,
    /** @return this event's result code. */
    public open val resultCode: Int,
  ) : ActivityCallbackEvent(Type.ACTIVITY_RESULT)

  /**
   * An [ActivityCallbackEvent] that encapsulates information from [Activity.onSaveInstanceState].
   */
  public open class SaveInstanceState(
    /** @return this event's outState data. */
    public open val outState: Bundle?,
  ) : ActivityCallbackEvent(Type.SAVE_INSTANCE_STATE)

  public companion object {
    private val LOW_MEMORY_EVENT = ActivityCallbackEvent(Type.LOW_MEMORY)

    /**
     * Creates an event for activity results.
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param resultData the result data intent
     * @return the created ActivityEvent.
     */
    @JvmStatic
    public fun createOnActivityResultEvent(
      requestCode: Int,
      resultCode: Int,
      resultData: Intent?,
    ): ActivityResult = ActivityResult(resultData, requestCode, resultCode)

    /**
     * Creates an activity event for a given type.
     *
     * @param type The type of event to get.
     * @return The corresponding ActivityEvent.
     */
    @JvmStatic
    public fun create(type: Type): ActivityCallbackEvent =
      when (type) {
        Type.LOW_MEMORY -> LOW_MEMORY_EVENT
        else ->
          throw IllegalArgumentException(
            "Use the createOn${type.name.toLowerCase().capitalize()}Event() method for this type!",
          )
      }

    /**
     * Creates an event for onSaveInstanceState.
     *
     * @param outState the outState bundle.
     * @return the created ActivityEvent.
     */
    @JvmStatic
    public fun createOnSaveInstanceStateEvent(outState: Bundle?): ActivityCallbackEvent =
      SaveInstanceState(outState)

    /**
     * Creates an event for [Activity.onTrimMemory]
     *
     * @param trimMemoryType that is passed by the activity callback
     * @return the created [TrimMemory]
     */
    @JvmStatic
    public fun createTrimMemoryEvent(trimMemoryType: Int): TrimMemory = TrimMemory(trimMemoryType)

    @JvmStatic
    public fun createPictureInPictureMode(isInPictureInPictureMode: Boolean): PictureInPictureMode =
      PictureInPictureMode(isInPictureInPictureMode)

    /**
     * Creates an event for onNewIntent.
     *
     * @param intent is the new intent received
     * @return the created [NewIntent].
     */
    @JvmStatic public fun createNewIntent(intent: Intent): NewIntent = NewIntent(intent)

    /**
     * Creates an event for onWindowFocusChanged
     *
     * @param hasFocus determines whether the window of this activity got focus or not
     * @return the newly created [WindowFocus]
     */
    @JvmStatic
    public fun createWindowFocusEvent(hasFocus: Boolean): WindowFocus = WindowFocus(hasFocus)
  }
}
