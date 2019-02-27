package com.badoo.mobile.plugin.generator

class Replacements {

    private val replaceFrom = mutableListOf<String>()
    private val replaceTo = mutableListOf<String>()

    fun add(from: String, to: String) {
        replaceFrom.add(from)
        replaceTo.add(to)
    }

    val fromArray: Array<String>
        get() = replaceFrom.toTypedArray()

    val toArray: Array<String>
        get() = replaceTo.toTypedArray()

}
