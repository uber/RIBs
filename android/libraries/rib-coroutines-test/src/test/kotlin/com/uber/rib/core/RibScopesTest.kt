package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException

@OptIn(ExperimentalCoroutinesApi::class)
class RibScopesTest {

    @get:Rule var rule = RibCoroutinesRule()

    @Test
    fun testScopeLifecycle()  = runBlockingTest {
        val interactor = FakeInteractor<Presenter, Router<*>>()
        interactor.attach()
        val job = interactor.coroutineScope.launch {
            while (isActive) {
                delay(5L)
            }
        }
        assertThat(job.isActive).isTrue()
        interactor.detach()
        assertThat(job.isActive).isFalse()
    }

    @Test
    fun testScopeLifecycleWithTestScope()  = runBlockingTest {
        val interactor = FakeInteractor<Presenter, Router<*>>()
        interactor.attach()
        interactor.enableTestCoroutineScopeOverride()

        val job = interactor.coroutineScope.launch {
            while (isActive) {
                delay(5L)
            }
        }
        assertThat(job.isActive).isTrue()
        interactor.detach()
        assertThat(job.isActive).isFalse()
    }

    @Test()
    fun testScopeCaching() {

        val interactor1 = FakeInteractor<Presenter, Router<*>>()
        val interactor2 = FakeInteractor<Presenter, Router<*>>()
        interactor1.attach()
        interactor2.attach()

        val interactor1mainScope1 = interactor1.coroutineScope
        val interactor1mainScope2 = interactor1.coroutineScope
        val interactor2mainScope1 = interactor2.coroutineScope

        assertThat(interactor1mainScope1).isEqualTo(interactor1mainScope2)
        assertThat(interactor1mainScope1).isNotEqualTo(interactor2mainScope1)
    }

    @Test(expected = RuntimeException::class)
    fun testUncaughtHandler() = runBlockingTest {
        val handler = TestCoroutineExceptionHandler()
        RibCoroutinesConfig.exceptionHandler = handler

        val interactor = FakeInteractor<Presenter, Router<*>>()
        interactor.attach()
        interactor.coroutineScope.launch {
            throw RuntimeException("mainScope failed")
        }
        handler.cleanupTestCoroutines()
    }

    @Test(expected = RuntimeException::class)
    fun testException() = runBlockingTest {

        val interactor = FakeInteractor<Presenter, Router<*>>()
        interactor.enableTestCoroutineScopeOverride()
        interactor.attach()
        interactor.coroutineScope.launch {
            throw RuntimeException("mainScope failed")
        }
        interactor.testCoroutineScopeOverride!!.cleanupTestCoroutines()
    }

    @Test()
    fun testSetTestScopeOverride() {

        val interactor = FakeInteractor<Presenter, Router<*>>()
        interactor.attach()

        assertThat(interactor.testCoroutineScopeOverride).isNull()

        interactor.enableTestCoroutineScopeOverride()
        val testScope = interactor.testCoroutineScopeOverride
        val realScope = interactor.coroutineScope
        assertThat(testScope).isInstanceOf(TestCoroutineScope::class.java)
        assertThat(testScope).isEqualTo(realScope)

        interactor.disableTestCoroutineScopeOverride()
        val testScope2 = interactor.testCoroutineScopeOverride
        val realScope2 = interactor.coroutineScope
        assertThat(testScope2).isNull()
        assertThat(realScope2).isNotInstanceOf(TestCoroutineScope::class.java)

    }
}