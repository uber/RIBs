/*
 * Copyright (C) 2023. Uber Technologies
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

/**
 * Reports duration of critical RIB events like Interactor.didBecomeActive/willResignActive or
 * Presenter.didLoad/willUnload
 */
public object RibLogger {

  private var ribMonitoringListener: RibMonitoringListener? = null

  /**
   * Initialize your monitoring at the earliest entry point of the app.
   *
   * This should only be set once
   */
  @JvmStatic
  public fun initialize(ribMonitoringListener: RibMonitoringListener) {
    this.ribMonitoringListener = ribMonitoringListener
  }

  /**
   * Will report Rib event duration information only when [RibMonitoringListener] is specified (via
   * RibLogger.initializeRibMonitor)
   */
  @JvmStatic
  public fun logRibEvent(
    className: String,
    eventType: RibMonitorType,
    totalBindingDurationMilli: Long,
  ) {
    ribMonitoringListener?.let {
      val ribMonitorData =
        RibMonitorData(
          className,
          eventType,
          Thread.currentThread().name,
          totalBindingDurationMilli,
        )
      it.onRibEventCompleted(ribMonitorData)
    }
  }
}

public enum class RibMonitorType {
  INTERACTOR_DID_BECOME_ACTIVE,
  INTERACTOR_WILL_RESIGN_ACTIVE,
  PRESENTER_DID_LOAD,
  PRESENTER_WILL_UNLOAD,
}

public data class RibMonitorData(
  /** Related RIB class name */
  val className: String,

  /** E.g. Interactor.didBecomeActive, Presenter.willLoad */
  val ribEventMonitorType: RibMonitorType,

  /** Reports the current thread name where Rib Event happen (should mainly be main thread) */
  val threadName: String,

  /** Total binding duration in milliseconds of Worker.onStart/onStop */
  val totalBindingDurationMilli: Long,
)

/** Reports duration of related RibEvents (e.g. Interactor.didBecomeActive/Presenter.diLoad) */
public interface RibMonitoringListener {
  public fun onRibEventCompleted(ribMonitorData: RibMonitorData)
}
