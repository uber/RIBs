package com.uber.rib.core

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.autoDispose
import io.reactivex.Completable
import io.reactivex.CompletableSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
        /**
         * Allows overriding [ScopeProvider.coroutineScope] with a [TestCoroutineScope] for testing
         */
val ScopeProvider.testCoroutineScopeOverride : TestCoroutineScope?
    //Due to custom friend path usage, reference to LazyCoroutineScope will stay red in IDE
    get() = synchronized(LazyCoroutineScope.values) {
        val testScope = LazyCoroutineScope.values[this]
        return if (testScope != null && testScope is TestCoroutineScope) testScope else null
    }

@ExperimentalCoroutinesApi
fun ScopeProvider.enableTestCoroutineScopeOverride(context : CoroutineContext = SupervisorJob()) = synchronized(LazyCoroutineScope.values) {
    LazyCoroutineScope.values[this] = asTestCoroutineScope(context)
}

fun ScopeProvider.disableTestCoroutineScopeOverride() = synchronized(LazyCoroutineScope.values) {
    LazyCoroutineScope.values.remove(this)
}

@ExperimentalCoroutinesApi
fun ScopeProvider.asTestCoroutineScope(context: CoroutineContext = SupervisorJob()): TestCoroutineScope {
    return requestScope().asTestCoroutineScope(context)
}


@ExperimentalCoroutinesApi
fun CompletableSource.asTestCoroutineScope(context: CoroutineContext = SupervisorJob()): TestCoroutineScope {
    val scope = TestCoroutineScope(context)
    Completable.wrap(this)
            .autoDispose(scope)
            .subscribe({ scope.cancel() }) { e -> scope.cancel("OnError", e) }

    return scope
}