package com.uber.rib.core

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.coroutinesinterop.asCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val ScopeProvider.ribScope: CoroutineScope
    get() = asCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)