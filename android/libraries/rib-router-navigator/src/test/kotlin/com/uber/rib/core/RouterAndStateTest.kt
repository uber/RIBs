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

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RouterAndStateTest {

  private val state = mock<RouterNavigatorState>()
  private val attachTransition =
    mock<RouterNavigator.AttachTransition<Router<*>, RouterNavigatorState>> {
      on { buildRouter() } doAnswer { mock() } // Creates new instance per each invocation
    }
  private val detachTransition =
    mock<RouterNavigator.DetachTransition<Router<*>, RouterNavigatorState>>()
  private val detachCallback =
    mock<RouterNavigator.DetachCallback<Router<*>, RouterNavigatorState>>()

  @Test
  fun router_whenStateIsNotCacheable_NotForcedToCache_shouldDestroyRouterAfterDetach() {
    whenever(state.isCacheable()).doReturn(false)

    val routerAndState =
      RouterAndState(state, attachTransition, detachTransition, forceRouterCaching = false)

    val router1 = routerAndState.router
    routerAndState.onPostDetachFromHost(mock(), false)
    val router2 = routerAndState.router

    assertThat(router1).isNotSameAs(router2)
  }

  @Test
  fun router_whenStateIsCacheable_NotForcedToCache_shouldReuseRouterOnNextAttach() {
    whenever(state.isCacheable()).doReturn(true)

    val routerAndState =
      RouterAndState(state, attachTransition, detachTransition, forceRouterCaching = false)

    val router1 = routerAndState.router
    routerAndState.onPostDetachFromHost(mock(), false)
    val router2 = routerAndState.router

    assertThat(router1).isSameAs(router2)
  }

  @Test
  fun router_whenStateIsNotCacheable_ForcedToCache_shouldReuseRouterOnNextAttach() {
    whenever(state.isCacheable()).doReturn(false)

    val routerAndState =
      RouterAndState(state, attachTransition, detachTransition, forceRouterCaching = true)

    val router1 = routerAndState.router
    routerAndState.onPostDetachFromHost(mock(), false)
    val router2 = routerAndState.router

    assertThat(router1).isSameAs(router2)
  }

  @Test
  fun router_whenStateIsCacheable_ForcedToCache_shouldReuseRouterOnNextAttach() {
    whenever(state.isCacheable()).doReturn(true)

    val routerAndState =
      RouterAndState(state, attachTransition, detachTransition, forceRouterCaching = true)

    val router1 = routerAndState.router
    routerAndState.onPostDetachFromHost(mock(), false)
    val router2 = routerAndState.router

    assertThat(router1).isSameAs(router2)
  }

  @Test
  fun willAttachToHost_shouldCallProvidedAttachCallback() {
    val routerAndState = RouterAndState(state, attachTransition, detachTransition)

    routerAndState.willAttachToHost(null, false)

    verify(attachTransition)
      .willAttachToHost(same(routerAndState.router), eq(null), same(state), eq(false))
  }

  @Test
  fun willDetachFromHost_whenDetachTransactionIsNotCallback_shouldCallProvidedDetachCallback() {
    val routerAndState = RouterAndState(state, attachTransition, detachTransition)

    routerAndState.willDetachFromHost(null, false)

    verify(detachTransition)
      .willDetachFromHost(same(routerAndState.router), same(state), eq(null), eq(false))
  }

  @Test
  fun willDetachFromHost_whenDetachTransactionIsCallback_shouldCallProvidedDetachCallback() {
    val routerAndState = RouterAndState(state, attachTransition, detachCallback)

    routerAndState.willDetachFromHost(null, false)

    verify(detachCallback)
      .willDetachFromHost(same(routerAndState.router), same(state), eq(null), eq(false))
  }

  @Test
  fun willDetachFromHost_whenDetachTransactionIsNotCallback_shouldCallProvidedDetachTransaction() {
    val routerAndState = RouterAndState(state, attachTransition, detachTransition)

    val router1 = routerAndState.router
    routerAndState.willDetachFromHost(null, false)

    verify(detachTransition).willDetachFromHost(same(router1), same(state), eq(null), eq(false))
  }

  @Test
  fun onPostDetachFromHost_shouldCallProvidedDetachCallback() {
    val routerAndState = RouterAndState(state, attachTransition, detachCallback)

    val router1 = routerAndState.router
    routerAndState.onPostDetachFromHost(null, false)

    verify(detachCallback).onPostDetachFromHost(same(router1), eq(null), eq(false))
  }

  @Test
  fun router_whenCurrentlyDestroyingTheInstance_shouldCallBuilderTwice() {
    val createdRouters = mutableSetOf<Router<*>>()
    val destroyedRouters = mutableSetOf<Router<*>>()

    whenever(attachTransition.buildRouter()).doAnswer {
      mock<Router<*>>().apply(createdRouters::add)
    }
    whenever(detachCallback.onPostDetachFromHost(any(), anyOrNull(), any())).then {
      destroyedRouters.add(it.arguments[0] as Router<*>)
    }

    val routerAndState =
      RouterAndState(
        state,
        attachTransition,
        detachCallback,
      )

    val threadsCount = 10
    val latch = CountDownLatch(threadsCount)

    // Create 10 threads with 1000 routers usage flows inside each
    repeat(threadsCount) {
      thread {
        repeat(1000) {
          routerAndState.willAttachToHost(null, false)
          routerAndState.willDetachFromHost(null, false)
          routerAndState.onPostDetachFromHost(null, false)
        }
        latch.countDown()
      }
    }

    latch.await()

    // Verify that all attached routers were detached
    assertThat(createdRouters.subtract(destroyedRouters)).isEmpty()
  }
}
