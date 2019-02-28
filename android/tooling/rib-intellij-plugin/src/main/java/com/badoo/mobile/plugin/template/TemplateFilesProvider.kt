package com.badoo.mobile.plugin.template

import com.badoo.mobile.plugin.generator.SourceSet
import com.badoo.mobile.plugin.template.file.ResourceTemplateFile
import java.lang.IllegalStateException
import java.nio.file.Paths

class TemplateFilesProvider(
    private val resourceProvider: ResourceProvider,
    private val metaInformationProvider: MetaInformationProvider
) {

    val templateFiles: Map<String, Map<SourceSet, List<ResourceTemplateFile>>> by lazy {
        load()
    }

    private fun load(): Map<String, Map<SourceSet, List<ResourceTemplateFile>>> {
        val templateFiles = resourceProvider.getResourceListing("templates/")

        val templateIdToFiles = templateFiles.groupBy {
            val templateId = it.removePrefix("templates/").substringBefore('/')
            templateId
        }

        return metaInformationProvider.templates.map { template ->
            val templateResources = templateIdToFiles[template.id]
                ?: throw IllegalStateException("Files for template with id ${template.id} not found")
            val sourceSetToFiles = templateResources
                .map {
                    val sourceSet = it.removePrefix("templates/${template.id}/").substringBefore('/')
                    val path = Paths.get(it.removePrefix("templates/${template.id}/$sourceSet/"))
                    val subDirectory = path.parent?.toString() ?: ""
                    val fileName = path.fileName.toString()

                    ResourceTemplateFile(
                        sourceSet = SourceSet.fromId(sourceSet),
                        directory = subDirectory,
                        fileName = fileName,
                        fullResourcePath = "/$it"
                    )
                }
                .groupBy {
                    it.sourceSet
                }
            template.id to sourceSetToFiles
        }.toMap()
    }
}
