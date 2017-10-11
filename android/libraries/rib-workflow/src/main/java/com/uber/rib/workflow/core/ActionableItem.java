package com.uber.rib.workflow.core;

import com.uber.rib.core.lifecycle.InteractorEvent;

import io.reactivex.Observable;

/** Represents an item that {@link Step} operations can be performed on. */
public interface ActionableItem {

  /**
   * @return a lifecycle observable that can be observed so a workflow knows when to start a step
   *     for this actionable item.
   */
  Observable<InteractorEvent> lifecycle();
}
