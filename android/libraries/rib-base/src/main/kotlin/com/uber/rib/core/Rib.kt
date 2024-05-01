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

/** Holds configuration and settings for riblets. */
public open class Rib {

  /** Responsible for app-specific riblet configuration. */
  public interface Configuration {
    /**
     * Called when there is a non-fatal error in the RIB framework. Consumers should route this data
     * to a place where it can be monitored (crash reporting, monitoring, etc.).
     *
     * If no configuration is set, the default implementation of this will crash the app when there
     * is a non-fatal error.
     *
     * @param errorMessage an error message that describes the error.
     * @param throwable an optional throwable.
     */
    public fun handleNonFatalError(errorMessage: String, throwable: Throwable?)

    /**
     * Called when there is a non-fatal warning in the RIB framework. Consumers should route this
     * data to a place where it can be monitored (crash reporting, monitoring, etc.).
     *
     * NOTE: This API is used in a slightly different way than the
     * [ ][Configuration.handleNonFatalError] error method. Non-fatal errors should never happen,
     * warnings however can happen in certain conditions.
     *
     * @param warningMessage an error message that describes the error.
     * @param throwable an optional throwable.
     */
    public fun handleNonFatalWarning(warningMessage: String, throwable: Throwable?)

    /**
     * Called when there is a message that should be logged for debugging. Consumers should route
     * this data to a debug logging location.
     *
     * If no configuration is set, the default implementation of this will drop the messages.
     *
     * @param format Message format - See [String.format]
     * @param args Arguments to use for printing the message.
     */
    public fun handleDebugMessage(format: String, vararg args: Any?)
  }

  /** Default, internal implementation that is used when host app does not set a configuration. */
  private class DefaultConfiguration : Configuration {
    override fun handleNonFatalError(errorMessage: String, throwable: Throwable?) {
      throw RuntimeException(errorMessage, throwable)
    }

    override fun handleNonFatalWarning(warningMessage: String, throwable: Throwable?) {}
    override fun handleDebugMessage(format: String, vararg args: Any?) {}
  }

  public companion object {
    private var configuration: Configuration? = null

    /**
     * Sets the configuration to use in the application. This can only be called once before any RIB
     * code is used. Calling it twice, or calling it after using RIB code will throw an exception.
     *
     * @param configurationToSet to set.
     */
    @JvmStatic
    public fun setConfiguration(configurationToSet: Configuration) {
      if (configuration == null) {
        configuration = configurationToSet
      } else {
        if (configuration is DefaultConfiguration) {
          throw IllegalStateException("Attempting to set a configuration after using RIB code.")
        } else {
          throw IllegalStateException(
            "Attempting to set a configuration after one has previously been set.",
          )
        }
      }
    }

    @JvmStatic
    public fun getConfiguration(): Configuration {
      if (configuration == null) {
        configuration = DefaultConfiguration()
      }
      return configuration!!
    }
  }
}
