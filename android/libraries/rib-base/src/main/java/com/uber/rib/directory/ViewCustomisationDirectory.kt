package com.uber.rib.directory

import kotlin.reflect.KClass

open class ViewCustomisationDirectory : MutableDirectory {

    private val map: MutableMap<Any, Any> = hashMapOf()

    inline fun <reified T : Any> put(value: T) {
        put(T::class, value)
    }

    override fun <T : Any> put(key: KClass<T>, value: T) {
        map[key] = value
    }

    override fun <T : Any> get(key: KClass<T>): T? =
        map[key] as? T
}
