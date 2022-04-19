package com.uber.rib.core

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

//TODO add weak hashmap to hold values of getters.

val ScopeProvider.defaultScope: CoroutineScope
    get() = asCoroutineScope(SupervisorJob() + RibDispatchers.Default)

val ScopeProvider.mainScope: CoroutineScope
    get() = asCoroutineScope(SupervisorJob() + RibDispatchers.Main)

val ScopeProvider.ioScope: CoroutineScope
    get() = asCoroutineScope(SupervisorJob() + RibDispatchers.IO)

val ScopeProvider.unconfinedScope: CoroutineScope
    get() = asCoroutineScope(SupervisorJob() + RibDispatchers.Unconfined)


