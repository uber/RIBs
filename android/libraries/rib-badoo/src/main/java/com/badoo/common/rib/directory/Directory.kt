package com.badoo.common.rib.directory

import kotlin.reflect.KClass

interface Directory {

    fun <T : Any> get(key: KClass<T>) : T?
}
