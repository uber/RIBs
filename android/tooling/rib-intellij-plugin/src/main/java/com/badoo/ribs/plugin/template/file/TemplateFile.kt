package com.badoo.ribs.plugin.template.file

import com.badoo.ribs.plugin.generator.SourceSet

interface TemplateFile {
    val sourceSet: SourceSet
    val directory: String
    val fileName: String
    val body: String
}
