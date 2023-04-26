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

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.functions.Function
import java.util.concurrent.Callable
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule to set AndroidSchedulers for tests. Inlined into this module to facilitate testing.
 *
 * @param restoreHandlers if true, the rule will save off the original schedulers and restore them
 *   after. Almost always want this to be false and is so by default.
 */
class AndroidSchedulersRule
@JvmOverloads
constructor(
  private val restoreHandlers: Boolean = false,
) : TestWatcher() {

  private val delegatingMainThreadScheduler =
    DelegatingScheduler.forType(DelegatingScheduler.SchedulerType.MAIN_THREAD)
  private val originalInitMainThreadInitHandler: Function<Callable<Scheduler>, Scheduler>? = null
  private val originalMainThreadHandler: Function<Scheduler, Scheduler>? = null

  override fun starting(description: Description) {
    if (restoreHandlers) {
      // https://github.com/ReactiveX/RxAndroid/pull/358
      //            originalInitMainThreadInitHandler =
      // RxAndroidPlugins.getInitMainThreadScheduler();
      //            originalMainThreadHandler = RxAndroidPlugins.getMainThreadScheduler();
    }
    RxAndroidPlugins.reset()
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { delegatingMainThreadScheduler }
    RxAndroidPlugins.setMainThreadSchedulerHandler { delegatingMainThreadScheduler }
  }

  override fun finished(description: Description) {
    RxAndroidPlugins.reset()
    if (restoreHandlers) {
      // https://github.com/ReactiveX/RxAndroid/pull/358
      //
      // RxAndroidPlugins.setInitMainThreadSchedulerHandler(originalInitMainThreadInitHandler);
      //            RxAndroidPlugins.setMainThreadSchedulerHandler(originalMainThreadHandler);
    }
  }

  /**
   * Replaces the main thread scheduler with a new scheduler.
   *
   * @param scheduler to replace the main thread scheduler with.
   */
  @Synchronized
  fun setMainThreadScheduler(scheduler: Scheduler) {
    delegatingMainThreadScheduler.setActiveScheduler(scheduler)
  }
}
