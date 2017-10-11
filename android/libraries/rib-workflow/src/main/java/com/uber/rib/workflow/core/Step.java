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

import com.uber.rib.core.Optional;
import com.uber.rib.core.lifecycle.InteractorEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Represents a unit of work for workflows.
 *
 * @param <T> type of return value (if any) for this step.
 * @param <A> type of {@link ActionableItem} this step returns when finished.
 */
public class Step<T, A extends ActionableItem> {

  private final Single<Optional<Data<T, A>>> stepDataSingle;

  /**
   * Constructor.
   *
   * @param stepDataSingle - a single that returns a result for this step.
   */
  private Step(Single<Optional<Data<T, A>>> stepDataSingle) {
    this.stepDataSingle = stepDataSingle;
  }

  /**
   * Create a new step with a single that always returns a value.
   *
   * @param stepDataSingle - a single that returns a result for this step.
   * @param <T> type of return value (if any) for this step.
   * @param <A> type of {@link ActionableItem} this step returns when finished
   * @return a new {@link Step}.
   */
  public static <T, A extends ActionableItem> Step<T, A> from(Single<Data<T, A>> stepDataSingle) {
    return new Step<>(
        stepDataSingle.map(
            new Function<Data<T, A>, Optional<Data<T, A>>>() {
              @Override
              public Optional<Data<T, A>> apply(Data<T, A> data) throws Exception {
                return Optional.of(data);
              }
            }));
  }

  /**
   * Create a new step with a single that can emit an absent result.
   *
   * <p>Absent results should be used when a step could not complete its action and can not move
   * forward.
   *
   * @param stepDataSingle - a single that returns a result for this step.
   * @param <T> type of return value (if any) for this step.
   * @param <A> type of {@link ActionableItem} this step returns when finished
   * @return a new {@link Step}.
   */
  public static <T, A extends ActionableItem> Step<T, A> fromOptional(
      Single<Optional<Data<T, A>>> stepDataSingle) {
    return new Step<>(stepDataSingle);
  }

  /**
   * Chains another step to be performed after this step completes. If the previous step results in
   * an error and does not emit a new actionable item, future chained onStep calls will not be
   * called.
   *
   * @param func to return the next step when this current step completes. This function will
   *     receive the result of the previous step and the next actionable item to take an action on.
   * @param <TNewValueType> the value type returned by the next step.
   * @param <TNewActionableItem> the actionable item type returned by the next step.
   * @return a {@link Step} to chain more calls to.
   */
  public <TNewValueType, TNewActionableItem extends ActionableItem>
      Step<TNewValueType, TNewActionableItem> onStep(
          final BiFunction<T, A, Step<TNewValueType, TNewActionableItem>> func) {
    return new Step<>(
        asObservable()
            .flatMap(
                new Function<
                    Optional<Data<T, A>>,
                    Observable<Optional<Data<TNewValueType, TNewActionableItem>>>>() {
                  @Override
                  public Observable<Optional<Data<TNewValueType, TNewActionableItem>>> apply(
                      Optional<Data<T, A>> dataOptional) throws Exception {
                    if (dataOptional.isPresent()) {
                      Data<T, A> data = dataOptional.get();
                      return func.apply(data.value, data.actionableItem).asObservable();
                    } else {
                      return Observable.just(
                          Optional.<Data<TNewValueType, TNewActionableItem>>absent());
                    }
                  }
                })
            .singleOrError());
  }

  protected Observable<Optional<Data<T, A>>> asObservable() {
    final Observable<Optional<Data<T, A>>> cachedObservable =
        stepDataSingle.toObservable().observeOn(AndroidSchedulers.mainThread()).cache();

    return cachedObservable.flatMap(
        new Function<Optional<Data<T, A>>, ObservableSource<Optional<Data<T, A>>>>() {
          @Override
          public ObservableSource<Optional<Data<T, A>>> apply(Optional<Data<T, A>> dataOptional)
              throws Exception {
            if (dataOptional.isPresent()) {
              A actionableItem = dataOptional.get().actionableItem;
              return actionableItem
                  .lifecycle()
                  .filter(
                      new Predicate<InteractorEvent>() {
                        @Override
                        public boolean test(InteractorEvent interactorEvent) throws Exception {
                          return interactorEvent == InteractorEvent.ACTIVE;
                        }
                      })
                  .zipWith(
                      cachedObservable,
                      new BiFunction<
                          InteractorEvent, Optional<Data<T, A>>, Optional<Data<T, A>>>() {
                        @Override
                        public Optional<Data<T, A>> apply(
                            InteractorEvent interactorEvent, Optional<Data<T, A>> dataOptional)
                            throws Exception {
                          return dataOptional;
                        }
                      });
            } else {
              return Observable.just(Optional.<Data<T, A>>absent());
            }
          }
        });
  }

  protected Observable<Optional<T>> asResultObservable() {
    return asObservable()
        .map(
            new Function<Optional<Data<T, A>>, Optional<T>>() {
              @Override
              public Optional<T> apply(Optional<Data<T, A>> dataOptional) {
                if (dataOptional.isPresent()) {
                  return Optional.of(dataOptional.get().getValue());
                } else {
                  return Optional.absent();
                }
              }
            });
  }

  /**
   * Data model for the result of a step.
   *
   * @param <T> type of return value (if any) for this step.
   * @param <A> type of {@link ActionableItem} this step returns when finished.
   */
  public static class Data<T, A extends ActionableItem> {

    private final T value;
    private final A actionableItem;

    /**
     * Constructor
     *
     * @param value for this instance.
     * @param actionableItem for this instance.
     */
    public Data(T value, A actionableItem) {
      this.value = value;
      this.actionableItem = actionableItem;
    }

    /**
     * Convenience function to create a {@link Step.Data} instance that does not have a return value
     * type.
     *
     * @param actionableItem to advance to.
     * @param <A> type of actionable item.
     * @return a new {@link Step.Data} instance.
     */
    public static <A extends ActionableItem> Data<NoValue, A> toActionableItem(A actionableItem) {
      return new Data<>(NoValueHolder.INSTANCE, actionableItem);
    }

    T getValue() {
      return value;
    }
  }

  /** Used to indicate that a step has no return value. */
  public static class NoValue {

    private NoValue() {}
  }

  /** Initialization On Demand Singleton for {@link NoValue}. */
  private static class NoValueHolder {

    private static final NoValue INSTANCE = new NoValue();
  }
}
