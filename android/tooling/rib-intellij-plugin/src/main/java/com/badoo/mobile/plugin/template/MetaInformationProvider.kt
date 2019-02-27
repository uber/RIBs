package com.badoo.mobile.plugin.template

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalStateException

class MetaInformationProvider(
    private val resourceProvider: ResourceProvider
) {

    val templates: List<Template>
        get() = _templates

    private val gson: Gson by lazy { Gson() }

    private val _templates: List<Template> by lazy {
        val templatesMeta = resourceProvider
            .getResourceAsStream(TEMPLATE_META_FILE_PATH)
            ?.reader()
            ?.readText()
            ?: throw IllegalStateException("Templates meta information not found at $TEMPLATE_META_FILE_PATH")

        val templatesListType = object : TypeToken<List<Template>>() {}.type
        gson.fromJson<List<Template>>(templatesMeta, templatesListType)
    }

    companion object {
        private const val TEMPLATE_META_FILE_PATH = "/templates_meta.json"
    }
}
