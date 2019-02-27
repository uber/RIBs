package com.badoo.mobile.plugin.template.file

import com.badoo.mobile.plugin.generator.SourceSet

interface TemplateFile {
    val sourceSet: SourceSet
    val directory: String
    val fileName: String
    val body: String
}
