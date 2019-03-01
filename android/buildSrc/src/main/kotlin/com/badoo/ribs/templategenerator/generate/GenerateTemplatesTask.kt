package com.badoo.ribs.templategenerator.generate

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidSourceSet
import com.google.gson.Gson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

@CacheableTask
open class GenerateTemplatesTask : DefaultTask() {

    @Input
    lateinit var templates: List<Template>

    @OutputDirectory
    lateinit var outputDirectory: File

    private val gson = Gson()

    @TaskAction
    fun generate() {
        project.delete(outputDirectory)

        templates.forEach { template ->
            val templateFiles = getTemplateFiles(template)
            templateFiles.forEach { sourceSet, directories ->
                if (sourceSet == RESOURCES_SOURCE_SET) {
                    val allowedResources = template.resources.toSet()
                    directories
                        .map { project.fileTree(it).files }
                        .flatten()
                        .filter { allowedResources.contains(it.name) }
                        .forEach { file -> writeTemplateResourceFile(template, sourceSet, file) }
                } else {
                    directories
                        .forEach { directory ->
                            project.fileTree(directory).visit {
                                if (it.file.isFile && it.file.isFileInPackage(template.sourcePackage, directory)) {
                                    writeTemplateSourceFile(template, sourceSet, directory, it.file)
                                }
                            }
                        }
                }
            }
        }

        writeTemplatesMeta(templates)
    }

    private fun writeTemplatesMeta(templates: List<Template>) {
        outputDirectory.mkdirs()
        outputDirectory.resolve("templates_meta.json").writeText(gson.toJson(templates))
    }

    private fun writeTemplateResourceFile(template: Template, sourceSet: String, resourceFile: File) {
        val relativePath = (resourceFile.parentFile.parentFile).toPath()
            .relativize(resourceFile.toPath()).toString()
        val targetFile = outputDirectory.resolve("templates", template.id, sourceSet, relativePath)
        val source = resourceFile.readText()
        targetFile.parentFile.mkdirs()
        targetFile.writeText(source)
    }

    private fun writeTemplateSourceFile(template: Template, sourceSet: String, baseSourceDirectory: File, sourceFile: File) {
        val relativePath = (baseSourceDirectory.resolve(*template.sourcePackage.split(".").toTypedArray())).toPath()
            .relativize(sourceFile.toPath()).toString()
        val targetFile = outputDirectory.resolve("templates", template.id, sourceSet, relativePath)
        val source = sourceFile.readText()
        targetFile.parentFile.mkdirs()
        targetFile.writeText(source)
    }

    private fun File.resolve(vararg parts: String): File {
        var result = this
        parts.forEach {
            result = File(result, it)
        }
        return result
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    fun getInputFiles(): List<File> = templates
        .map { getTemplateFiles(it).values }
        .flatten()
        .flatten()

    private fun getTemplateFiles(template: Template): Map<String, List<File>> {
        val androidExtension = template.fromProject.extensions.getByType(BaseExtension::class.java)
        val androidSourceSets: List<AndroidSourceSet> = androidExtension.sourceSets.asMap.map {
            it.value
        }

        val androidResourceDirectories = androidSourceSets.find { it.name == MAIN_SOURCE_SET }
            ?.res
            ?.srcDirs
            ?.toList() ?: throw IllegalStateException("Android resources not found")

        val sourceCodeDirectories = androidSourceSets
            .filter { SOURCE_CODE_SOURCE_SETS.contains(it.name) }
            .map { it.name to it.java.srcDirs.toList() }
            .toMap()

        return sourceCodeDirectories.plus("resources" to androidResourceDirectories)
    }

    private companion object {
        private val SOURCE_CODE_SOURCE_SETS = setOf(
            "main",
            "test",
            "androidTest"
        )
        private const val RESOURCES_SOURCE_SET = "resources"
        private const val MAIN_SOURCE_SET = "main"
    }

    private fun File.isFileInPackage(javaPackage: String, baseDirectory: File): Boolean {
        val relativePath = baseDirectory.toPath().relativize(this.toPath()).toString()
        val packagePath = javaPackage.replace('.', File.separatorChar)
        return relativePath.startsWith(packagePath)
    }
}
