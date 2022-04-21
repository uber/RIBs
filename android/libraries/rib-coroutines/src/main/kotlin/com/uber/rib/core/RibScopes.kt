package com.uber.rib.core

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.*
import kotlin.reflect.KProperty

val ScopeProvider.defaultScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + scopeName(this, "defaultScope") + RibDispatchers.Default)
}

val ScopeProvider.mainScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + scopeName(this, "mainScope") + RibDispatchers.Main.immediate)
}

val ScopeProvider.ioScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + scopeName(this, "ioScope") + RibDispatchers.IO)
}
val ScopeProvider.unconfinedScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + scopeName(this, "unconfinedScope") + RibDispatchers.Unconfined)
}

private fun scopeName(scopeProvider : ScopeProvider, scopeName: String) = CoroutineName("${scopeProvider::class.simpleName}:${scopeName}")

private class LazyWithReceiver<This,Return>(val initializer:This.()->Return)
{
    private val values = WeakHashMap<This,Return>()

    operator fun getValue(thisRef:This, property: KProperty<*>):Return = synchronized(values)
    {
        return values.getOrPut(thisRef) { thisRef.initializer() }
    }
}