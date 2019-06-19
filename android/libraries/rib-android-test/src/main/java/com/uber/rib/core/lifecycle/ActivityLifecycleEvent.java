/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core.lifecycle;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.Locale;

/** Lifecycle events that can be emitted by Activities. */
public class ActivityLifecycleEvent implements ActivityEvent {

  private static final ActivityLifecycleEvent START_EVENT = new ActivityLifecycleEvent(Type.START);
  private static final ActivityLifecycleEvent RESUME_EVENT = new ActivityLifecycleEvent(Type.RESUME);
  private static final ActivityLifecycleEvent PAUSE_EVENT = new ActivityLifecycleEvent(Type.PAUSE);
  private static final ActivityLifecycleEvent STOP_EVENT = new ActivityLifecycleEvent(Type.STOP);
  private static final ActivityLifecycleEvent DESTROY_EVENT = new ActivityLifecycleEvent(Type.DESTROY);

  private final Type type;

  private ActivityLifecycleEvent(Type type) {
    this.type = type;
  }

  /**
   * Creates an event for onCreate.
   *
   * @param stateData the instate bundle.
   * @return the created ActivityEvent.
   */
  public static ActivityLifecycleEvent.Create createOnCreateEvent(@Nullable Bundle stateData) {
    return new Create(stateData);
  }

  /**
   * Creates an activity event for a given type.
   *
   * @param type The type of event to get.
   * @return The corresponding ActivityEvent.
   */
  public static ActivityLifecycleEvent create(Type type) {
    switch (type) {
      case START:
        return START_EVENT;
      case RESUME:
        return RESUME_EVENT;
      case PAUSE:
        return PAUSE_EVENT;
      case STOP:
        return STOP_EVENT;
      case DESTROY:
        return DESTROY_EVENT;
      default:
        throw new IllegalArgumentException(
            "Use the createOn"
                + capitalize(type.name().toLowerCase(Locale.US))
                + "Event() method for this type!");
    }
  }

  /** @return this event's type. */
  @Override
  public Type getType() {
    return this.type;
  }

  private static String capitalize(final String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }

  /** Types of activity events that can occur. */
  public enum Type implements BaseType {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY,
  }

  /**
   * An {@link ActivityLifecycleEvent} that encapsulates information from {@link
   * Activity#onCreate(Bundle)}.
   */
  public static class Create extends ActivityLifecycleEvent {
    @Nullable private final Bundle savedInstanceState;

    private Create(@Nullable Bundle savedInstanceState) {
      super(Type.CREATE);
      this.savedInstanceState = savedInstanceState;
    }

    /** @return this event's savedInstanceState data. */
    @Nullable
    public Bundle getSavedInstanceState() {
      return savedInstanceState;
    }
  }
}
