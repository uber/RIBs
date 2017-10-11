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

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;

import static com.uber.rib.workflow.core.DelegatingScheduler.SchedulerType.MAIN_THREAD;

/** JUnit rule to set AndroidSchedulers for tests. Inlined into this
 * module to facilitate testing. */
class AndroidSchedulersRule extends TestWatcher {

  private DelegatingScheduler delegatingMainThreadScheduler =
      DelegatingScheduler.forType(MAIN_THREAD);

  private Function<Callable<Scheduler>, Scheduler> originalInitMainThreadInitHandler;
  private Function<Scheduler, Scheduler> originalMainThreadHandler;

  private final boolean restoreHandlers;

  public AndroidSchedulersRule() {
    this(false);
  }

  /**
   * @param restoreHandlers if true, the rule will save off the original schedulers and restore them
   *     after. Almost always want this to be false and is so by default.
   */
  @SuppressWarnings("CheckNullabilityTypes")
  public AndroidSchedulersRule(boolean restoreHandlers) {
    this.restoreHandlers = restoreHandlers;
  }

  @Override
  protected void starting(Description description) {
    if (restoreHandlers) {
      // https://github.com/ReactiveX/RxAndroid/pull/358
      //            originalInitMainThreadInitHandler =
      // RxAndroidPlugins.getInitMainThreadScheduler();
      //            originalMainThreadHandler = RxAndroidPlugins.getMainThreadScheduler();
    }
    RxAndroidPlugins.reset();
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(
        new Function<Callable<Scheduler>, Scheduler>() {
          @Override
          public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
            return delegatingMainThreadScheduler;
          }
        });
    RxAndroidPlugins.setMainThreadSchedulerHandler(
        new Function<Scheduler, Scheduler>() {
          @Override
          public Scheduler apply(Scheduler scheduler) throws Exception {
            return delegatingMainThreadScheduler;
          }
        });
  }

  @Override
  protected void finished(Description description) {
    RxAndroidPlugins.reset();
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
  public final synchronized void setMainThreadScheduler(Scheduler scheduler) {
    delegatingMainThreadScheduler.setActiveScheduler(scheduler);
  }
}