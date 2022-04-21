package com.uber.rib.core

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.*
import kotlin.reflect.KProperty

val ScopeProvider.defaultScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + CoroutineName("defaultScope") + RibDispatchers.Default)
}

val ScopeProvider.mainScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + CoroutineName("mainScope") + RibDispatchers.Main.immediate)
}

val ScopeProvider.ioScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + CoroutineName("ioScope") + RibDispatchers.IO)
}
val ScopeProvider.unconfinedScope by LazyWithReceiver<ScopeProvider, CoroutineScope> {
    asCoroutineScope(SupervisorJob() + CoroutineName("unconfinedScope") + RibDispatchers.Unconfined)
}

private class LazyWithReceiver<This,Return>(val initializer:This.()->Return)
{
    private val values = WeakHashMap<This,Return>()

    operator fun getValue(thisRef:This, property: KProperty<*>):Return = synchronized(values)
    {
        return values.getOrPut(thisRef) { thisRef.initializer() }
    }
}