package com.uber.rib.core.directory

import kotlin.reflect.KClass

interface Directory {

    fun <T : Any> get(key: KClass<T>) : T?
}
