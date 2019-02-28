package com.badoo.ribs.templategenerator

import groovy.lang.Closure
import org.gradle.api.Project

open class TemplateGeneratorExtension(private val project: Project) {

    val templates: MutableList<TemplateExtension> = mutableListOf()

    fun template(templateConfigurator: Closure<*>) {
        templates += TemplateExtension().apply {
            templateConfigurator.delegate = this
            templateConfigurator.call()
        }
    }

    class TemplateExtension {
        var id: String? = null
        var name: String? = null
        var fromProject: Project? = null
        var modulePackage: String? = null
        var sourcePackage: String? = null

        var resources: List<String>? = null

        val tokens: MutableList<Token> = mutableListOf()

        fun token(tokenConfigurator: Closure<*>) {
            val token = Token().apply {
                tokenConfigurator.delegate = this
                tokenConfigurator.resolveStrategy = Closure.DELEGATE_ONLY
                tokenConfigurator.call()
            }
            tokens += token
        }
    }

    class Token {
        var id: String? = null
        var name: String? = null
        var sourceValue: String? = null
    }
}
