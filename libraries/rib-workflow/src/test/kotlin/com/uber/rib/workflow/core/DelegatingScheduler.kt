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
package com.uber.rib.workflow.core

import androidx.annotation.VisibleForTesting
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class DelegatingScheduler
private constructor(
  @get:VisibleForTesting val schedulerType: SchedulerType,
) : Scheduler() {

  private val activeScheduler = AtomicReference(Schedulers.trampoline())

  @VisibleForTesting
  enum class SchedulerType {
    MAIN_THREAD,
  }

  override fun createWorker(): Worker {
    return activeScheduler().createWorker()
  }

  override fun now(unit: TimeUnit): Long {
    return activeScheduler().now(unit)
  }

  @VisibleForTesting
  @Synchronized
  fun activeScheduler(): Scheduler {
    return activeScheduler.get()
  }

  @Synchronized
  fun setActiveScheduler(activeScheduler: Scheduler) {
    this.activeScheduler.set(activeScheduler)
  }

  companion object {
    fun forType(schedulerType: SchedulerType) = DelegatingScheduler(schedulerType)
  }
}
