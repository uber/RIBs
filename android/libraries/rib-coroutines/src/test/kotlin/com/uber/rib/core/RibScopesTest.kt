package com.uber.rib.core

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RibScopesTest {

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        RibDispatchersConfig.delegate = DefaultRibDispatcherProvider(
                Default = TestCoroutineDispatcher(),
                Main = Dispatchers.Main,
                IO = TestCoroutineDispatcher(),
                Unconfined = TestCoroutineDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        RibDispatchersConfig.reset()
    }

    @Test
    fun testScopeLifecycle()  = runBlocking {
        val interactor = FakeInteractor<Presenter, Router<*>>()
        interactor.attach()
        val job = interactor.mainScope.launch {
            while (isActive) {
                delay(5L)
            }
        }
        assertThat(job.isActive).isTrue()
        interactor.detach()
        assertThat(job.isActive).isFalse()
    }

    @Test
    fun testScopeCaching() {

        val interactor1 = FakeInteractor<Presenter, Router<*>>()
        val interactor2 = FakeInteractor<Presenter, Router<*>>()
        interactor1.attach()
        interactor2.attach()

        val interactor1mainScope1 = interactor1.mainScope
        val interactor1mainScope2 = interactor1.mainScope
        val interactor2mainScope1 = interactor2.mainScope

        assertThat(interactor1mainScope1).isEqualTo(interactor1mainScope2)
        assertThat(interactor1mainScope1).isNotEqualTo(interactor2mainScope1)
    }


}