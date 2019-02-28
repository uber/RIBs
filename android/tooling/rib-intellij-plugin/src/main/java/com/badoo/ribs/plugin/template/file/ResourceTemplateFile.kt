package com.badoo.ribs.plugin.template.file

import com.badoo.ribs.plugin.generator.SourceSet

data class ResourceTemplateFile(override val sourceSet: SourceSet,
                                override val directory: String,
                                override val fileName: String,
                                private val fullResourcePath: String) : TemplateFile {

    override val body: String
        get() = this::class.java.getResourceAsStream(this.fullResourcePath).reader().readText()

}
