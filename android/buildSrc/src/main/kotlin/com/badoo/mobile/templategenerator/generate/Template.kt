package com.badoo.mobile.templategenerator.generate

import org.gradle.api.Project
import java.io.Serializable

class Template(
    val id: String,
    val name: String,
    @Transient val fromProject: Project,
    val modulePackage: String,
    val sourcePackage: String,
    val resources: List<String>,
    val tokens: List<Token>
) : Serializable
