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

import com.google.common.base.Optional
import com.uber.rib.core.lifecycle.InteractorEvent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction

/**
 * Represents a unit of work for workflows.
 *
 * @param <T> type of return value (if any) for this step.
 * @param <A> type of [ActionableItem] this step returns when finished.
 */
open class Step<T, A : ActionableItem>
private constructor(
  private val stepDataSingle: Single<Optional<Data<T, A>>>,
) {

  /**
   * Chains another step to be performed after this step completes. If the previous step results in
   * an error and does not emit a new actionable item, future chained onStep calls will not be
   * called.
   *
   * @param func to return the next step when this current step completes. This function will
   *   receive the result of the previous step and the next actionable item to take an action on.
   * @param <T2> the value type returned by the next step.
   * @param <A2> the actionable item type returned by the next step.
   * @return a [Step] to chain more calls to.
   */
  @SuppressWarnings("RxJavaToSingle") // Replace singleOrError() with firstOrError()
  open fun <T2, A2 : ActionableItem> onStep(func: BiFunction<T, A, Step<T2, A2>>): Step<T2, A2> {
    return Step(
      asObservable()
        .flatMap { data: Optional<Data<T, A>> ->
          if (data.isPresent) {
            func.apply(data.get().getValue(), data.get().actionableItem).asObservable()
          } else {
            Observable.just(Optional.absent())
          }
        }
        .singleOrError(),
    )
  }

  internal open fun asResultObservable(): Observable<Optional<T>> {
    return asObservable().map { data -> Optional.fromNullable(data.orNull()?.getValue()) }
  }

  internal open fun asObservable(): Observable<Optional<Data<T, A>>> {
    val cachedObservable: Observable<Optional<Data<T, A>>> =
      stepDataSingle.toObservable().observeOn(AndroidSchedulers.mainThread()).cache()
    return cachedObservable.flatMap { dataOptional: Optional<Data<T, A>> ->
      if (dataOptional.isPresent) {
        dataOptional
          .get()
          .actionableItem
          .lifecycle()
          .filter { interactorEvent -> interactorEvent === InteractorEvent.ACTIVE }
          .zipWith(cachedObservable) { _, data -> data }
      } else {
        Observable.just(Optional.absent())
      }
    }
  }

  /**
   * Data model for the result of a step.
   *
   * @param <T> type of return value (if any) for this step.
   * @param <A> type of [ActionableItem] this step returns when finished.
   * @param value for this instance.
   * @param actionableItem for this instance.
   */
  open class Data<T, A : ActionableItem>(private val value: T, internal val actionableItem: A) {

    internal open fun getValue() = value

    companion object {
      /**
       * Convenience function to create a [Step.Data] instance that does not have a return value
       * type.
       *
       * @param actionableItem to advance to.
       * @param <A> type of actionable item.
       * @return a new [Step.Data] instance. </A>
       */
      @JvmStatic
      fun <A : ActionableItem> toActionableItem(actionableItem: A): Data<NoValue, A> {
        return Data(NoValueHolder.INSTANCE, actionableItem)
      }
    }
  }

  /** Used to indicate that a step has no return value. */
  open class NoValue

  /** Initialization On Demand Singleton for [NoValue]. */
  private object NoValueHolder {
    val INSTANCE = NoValue()
  }

  companion object {
    /**
     * Create a new step with a single that always returns a value.
     *
     * @param stepDataSingle - a single that returns a result for this step.
     * @param <T> type of return value (if any) for this step.
     * @param <A> type of [ActionableItem] this step returns when finished
     * @return a new [Step].
     */
    @JvmStatic
    fun <T, A : ActionableItem> from(stepDataSingle: Single<Data<T, A>>): Step<T, A> {
      return Step(stepDataSingle.map { Optional.of(it) })
    }

    /**
     * Create a new step with a single that can emit an absent result.
     *
     * Absent results should be used when a step could not complete its action and can not move
     * forward.
     *
     * @param stepDataSingle - a single that returns a result for this step.
     * @param <T> type of return value (if any) for this step.
     * @param <A> type of [ActionableItem] this step returns when finished
     * @return a new [Step].
     */
    @JvmStatic
    fun <T, A : ActionableItem> fromOptional(
      stepDataSingle: Single<Optional<Data<T, A>>>,
    ): Step<T, A> = Step(stepDataSingle)
  }
}
