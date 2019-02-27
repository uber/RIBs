package com.badoo.mobile.plugin.util

import org.picocontainer.PicoContainer

inline fun <reified T> PicoContainer.get(): T = getComponentInstanceOfType(T::class.java) as T
