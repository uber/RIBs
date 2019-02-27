package com.badoo.mobile.plugin.generator

enum class SourceSet(
    val id: String
) {

    MAIN("main"),
    TEST("test"),
    ANDROID_TEST("androidTest"),
    RESOURCES("resources");

    companion object {
        fun fromId(id: String): SourceSet = values().find { it.id == id }
            ?: throw IllegalArgumentException("Source set with id $id not found")
    }
}
