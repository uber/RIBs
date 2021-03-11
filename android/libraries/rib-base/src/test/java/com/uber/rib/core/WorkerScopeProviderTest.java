package com.uber.rib.core;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.Relay;
import com.uber.rib.core.lifecycle.WorkerEvent;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

public class WorkerScopeProviderTest {

  private final Relay<WorkerEvent> lifecycle = BehaviorRelay.create();

  private TestObserver<Object> testObserver;

  @Before
  public void setUp() {
    WorkerScopeProvider workerScopeProvider = new WorkerScopeProvider(lifecycle.hide());
    testObserver = new TestObserver<>();
    workerScopeProvider.requestScope().subscribe(testObserver);
  }

  @Test
  public void byDefaultTestObserve_shouldNotComplete() {
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  @Test
  public void whenStartLifecycleEmitted_shouldNotComplete() {
    lifecycle.accept(WorkerEvent.START);
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  @Test
  public void whenStartLifecycleEmittedThenStopEventEmitted_shouldComplete() {
    lifecycle.accept(WorkerEvent.START);
    lifecycle.accept(WorkerEvent.STOP);
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }

  @Test
  public void whenStartLifecycleEmittedThenStartEventEmittedAgain_shouldAlsoComplete() {
    lifecycle.accept(WorkerEvent.START);
    lifecycle.accept(WorkerEvent.START);
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }
}