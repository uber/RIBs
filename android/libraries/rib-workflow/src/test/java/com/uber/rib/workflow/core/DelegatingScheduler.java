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

package com.uber.rib.workflow.core;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static androidx.annotation.RestrictTo.Scope.GROUP_ID;

@RestrictTo(GROUP_ID)
class DelegatingScheduler extends Scheduler {

  private final AtomicReference<Scheduler> activeScheduler =
      new AtomicReference<>(Schedulers.trampoline());
  private volatile SchedulerType schedulerType;

  @VisibleForTesting
  enum SchedulerType {
    MAIN_THREAD
  }

  static DelegatingScheduler forType(SchedulerType schedulerType) {
    return new DelegatingScheduler(schedulerType);
  }

  private DelegatingScheduler(SchedulerType schedulerType) {
    super();
    this.schedulerType = schedulerType;
  }

  @Override
  public Worker createWorker() {
    return activeScheduler().createWorker();
  }

  @Override
  public long now(TimeUnit unit) {
    return activeScheduler().now(unit);
  }

  @SuppressWarnings("CheckNullabilityTypes")
  @VisibleForTesting
  synchronized Scheduler activeScheduler() {
    return activeScheduler.get();
  }

  synchronized void setActiveScheduler(final Scheduler activeScheduler) {
    this.activeScheduler.set(activeScheduler);
  }

  @VisibleForTesting()
  SchedulerType getSchedulerType() {
    return schedulerType;
  }
}
