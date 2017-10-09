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
package com.uber.rib.core;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RouterTest {

  @Test
  public void didLoad_shouldBeCalledAfterInstantiation() {
    final AtomicBoolean didLoad = new AtomicBoolean(false);
    Router router =
        new Router<Interactor, InteractorComponent>(
            mock(InteractorComponent.class),
            mock(Interactor.class),
            mock(RibRefWatcher.class),
            Thread.currentThread()) {
          @Override
          protected void didLoad() {
            super.didLoad();
            didLoad.set(true);
          }
        };

    router.dispatchAttach(null);

    assertThat(didLoad.get()).isTrue();
  }
}
