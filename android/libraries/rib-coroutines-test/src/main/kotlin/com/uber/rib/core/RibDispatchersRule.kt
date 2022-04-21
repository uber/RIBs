package com.uber.rib.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RibDispatchersRule : TestRule {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(TestCoroutineDispatcher())
            RibDispatchersConfig.delegate = DefaultRibDispatcherProvider(
                    Default = TestCoroutineDispatcher(),
                    Main = Dispatchers.Main,
                    IO = TestCoroutineDispatcher(),
                    Unconfined = TestCoroutineDispatcher())

            Dispatchers.Main
            try {
                base.evaluate()
            } finally {
                Dispatchers.resetMain()
                RibDispatchersConfig.reset()
            }
        }
    }
}