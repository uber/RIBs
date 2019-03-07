package com.badoo.ribs.templategenerator

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.badoo.ribs.templategenerator.generate.GenerateTemplatesTask
import com.badoo.ribs.templategenerator.generate.Template
import com.badoo.ribs.templategenerator.generate.Token
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class TemplateGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension: TemplateGeneratorExtension = project.extensions.create("templates", TemplateGeneratorExtension::class.java, project)

        project.afterEvaluate {
            val outputDirectory = project.file("${project.buildDir}/generated-template-resources")

            extension.templates.forEach { template ->
                template.fromProject?.path?.let { projectPath ->
                    project.evaluationDependsOn(projectPath)
                }
            }

            val task = project.tasks.register("generateTemplates", GenerateTemplatesTask::class.java) { task ->
                task.templates = extension.templates.validateTemplatesAndMap()
                task.outputDirectory = outputDirectory
            }

            val sourceSets = (project.extensions.getByType(SourceSetContainer::class.java) as SourceSetContainer)
            sourceSets.getByName("main").output.dir(mapOf("buildBy" to task.name), outputDirectory)

            project.tasks.named("processResources").dependsOn(task)
        }
    }

    private fun List<TemplateGeneratorExtension.TemplateExtension>.validateTemplatesAndMap(): List<Template> =
        map {
            Template(
                id = it.id ?: throw IllegalArgumentException("Template id is mandatory"),
                name = it.name ?: throw IllegalArgumentException("Template name is mandatory"),
                fromProject = it.fromProject
                    ?: throw IllegalArgumentException("fromProject property is mandatory"),
                modulePackage = it.fromProject!!.androidPackageName ?: it.modulePackage
                    ?: throw IllegalArgumentException("modulePackage property is mandatory for library modules"),
                sourcePackage = it.sourcePackage
                    ?: throw IllegalArgumentException("sourcePackage property is mandatory"),
                resources = it.resources ?: emptyList(),
                tokens = it.tokens.validateTokensAndMap()
            )
        }

    private fun List<TemplateGeneratorExtension.Token>.validateTokensAndMap(): List<Token> = map {
        Token(
            id = it.id ?: throw IllegalArgumentException("Token id is mandatory"),
            name = it.name ?: throw IllegalArgumentException("Token name is mandatory"),
            sourceValue = it.sourceValue ?: throw IllegalArgumentException("Source token value is mandatory")
        )
    }

    private val Project.androidPackageName: String?
        get() {
            val androidExtension = extensions.getByType(BaseExtension::class.java)
            return androidExtension.defaultConfig.applicationId
        }
}
