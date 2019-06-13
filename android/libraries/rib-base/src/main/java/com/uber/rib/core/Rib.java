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
package com.uber.rib.core;

import androidx.annotation.Nullable;

/** Holds configuration and settings for riblets. */
public class Rib {

  @Nullable private static Configuration configuration = null;

  private Rib() {}

  /**
   * Sets the configuration to use in the application. This can only be called once before any RIB
   * code is used. Calling it twice, or calling it after using RIB code will throw an {@link
   * IllegalStateException}.
   *
   * @param configurationToSet to set.
   */
  public static void setConfiguration(Configuration configurationToSet) {
    if (configuration == null) {
      configuration = configurationToSet;
    } else {
      if (configuration instanceof DefaultConfiguration) {
        throw new IllegalStateException("Attempting to set a configuration after using RIB code.");
      } else {
        throw new IllegalStateException(
            "Attempting to set a configuration after one has previously been set.");
      }
    }
  }

  static Configuration getConfiguration() {
    if (configuration == null) {
      configuration = new DefaultConfiguration();
    }

    return configuration;
  }

  /** Responsible for app-specific riblet configuration. */
  public interface Configuration {

    /**
     * Called when there is a non-fatal error in the RIB framework. Consumers should route this data
     * to a place where it can be monitored (crash reporting, monitoring, etc.).
     *
     * <p>If no configuration is set, the default implementation of this will crash the app when
     * there is a non-fatal error.
     *
     * @param errorMessage an error message that describes the error.
     * @param throwable an optional throwable.
     */
    void handleNonFatalError(String errorMessage, @Nullable Throwable throwable);

    /**
     * Called when there is a non-fatal warning in the RIB framework. Consumers should route this
     * data to a place where it can be monitored (crash reporting, monitoring, etc.).
     *
     * <p>NOTE: This API is used in a slightly different way than the {@link
     * Configuration#handleNonFatalError(String, Throwable)} error method. Non-fatal errors should
     * never happen, warnings however can happen in certain conditions.
     *
     * @param warningMessage an error message that describes the error.
     * @param throwable an optional throwable.
     */
    void handleNonFatalWarning(String warningMessage, @Nullable Throwable throwable);

    /**
     * Called when there is a message that should be logged for debugging. Consumers should route
     * this data to a debug logging location.
     *
     * <p>If no configuration is set, the default implementation of this will drop the messages.
     *
     * @param format Message format - See {@link String#format(String, Object...)}
     * @param args Arguments to use for printing the message.
     */
    void handleDebugMessage(String format, @Nullable final Object... args);
  }

  /** Default, internal implementation that is used when host app does not set a configuration. */
  private static class DefaultConfiguration implements Configuration {

    @Override
    public void handleNonFatalError(String errorMessage, @Nullable Throwable throwable) {
      throw new RuntimeException(errorMessage, throwable);
    }

    @Override
    public void handleNonFatalWarning(String warningMessage, @Nullable Throwable throwable) {}

    @Override
    public void handleDebugMessage(String format, @Nullable Object... args) {}
  }
}
