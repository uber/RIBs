package com.uber.rib.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * RibCoroutinesRule is a Junit TestRule to act as a managed TestCoroutineScope in test and to facilitate install and cleanup of Test Dispatchers
 */
@ExperimentalCoroutinesApi
class RibCoroutinesRule(val ribDispatchers: TestRibDispatchers = TestRibDispatchers()) : TestWatcher(),
        TestCoroutineScope by TestCoroutineScope(ribDispatchers.Default) {

    override fun starting(description: Description) {
        ribDispatchers.installTestDispatchers()
    }

    override fun finished(description: Description) {
        cleanupTestCoroutines()
        ribDispatchers.cleanupTestDispatchers()
        ribDispatchers.resetTestDispatchers()
    }
}