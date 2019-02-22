package com.uber.rib.core.directory

import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.ViewFactory
import kotlin.reflect.KClass

open class ViewCustomisationDirectory : MutableDirectory {

    private val map: MutableMap<Any, Any> = mutableMapOf()

    inline fun <reified T : Any> put(value: T) {
        put(T::class, value)
    }

    override fun <T : Any> put(key: KClass<T>, value: T) {
        map[key] = value
    }

    override fun <T : Any> get(key: KClass<T>): T? =
        map[key] as? T
}

fun <T> inflateOnDemand(layoutResourceId: Int): ViewFactory<T> = object : ViewFactory<T> {
    override fun invoke(parentViewGroup: ViewGroup): T =
        inflate(parentViewGroup, layoutResourceId)
}

fun <T> inflate(parentViewGroup: ViewGroup, layoutResourceId: Int): T =
    LayoutInflater.from(parentViewGroup.context).inflate(layoutResourceId, parentViewGroup, false) as T
