package com.uber.rib.core

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty


val ScopeProvider.coroutineScope : CoroutineScope by LazyCoroutineScope {
    var context: CoroutineContext = SupervisorJob() + RibDispatchers.Main.immediate + CoroutineName("${this::class.simpleName}:coroutineScope")

    RibCoroutinesConfig.exceptionHandler?.let {
        context += RibCoroutinesConfig.exceptionHandler!!
    }
    asCoroutineScope(context)
}

internal class LazyCoroutineScope(val initializer: ScopeProvider.() -> CoroutineScope) {
    companion object {
        val values = WeakHashMap<ScopeProvider, CoroutineScope>()
    }
    operator fun getValue(thisRef: ScopeProvider, property: KProperty<*>): CoroutineScope = synchronized(values)
    {
        return values.getOrPut(thisRef) { thisRef.initializer() }
    }
}