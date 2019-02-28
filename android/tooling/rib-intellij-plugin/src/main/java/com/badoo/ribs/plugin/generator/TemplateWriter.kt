package com.badoo.ribs.plugin.generator

import com.badoo.ribs.plugin.action.SourceSetDirectoriesProvider
import com.badoo.ribs.plugin.template.file.TemplateFile
import com.badoo.ribs.plugin.util.applyReplacements
import com.intellij.ide.util.DirectoryUtil
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import com.intellij.util.IncorrectOperationException
import java.io.File

class TemplateWriter(private val templateFiles: Map<SourceSet, List<TemplateFile>>) {

    fun writeFiles(project: Project,
                   sourceSetDirectories: SourceSetDirectoriesProvider,
                   replacements: Replacements) {

        templateFiles.forEach { sourceSet, files ->
            val targetDirectory = sourceSetDirectories.getDirectory(sourceSet)
            writeFiles(targetDirectory, project, files, replacements)
        }
    }

    private fun writeFiles(targetDirectory: PsiDirectory,
                           project: Project,
                           files: List<TemplateFile>,
                           fileReplacements: Replacements) {
        files.forEach { templateResource ->
            val body = templateResource.body
            project.createSourceFile(
                targetDirectory,
                templateResource.directory.applyReplacements(fileReplacements),
                templateResource.fileName.applyReplacements(fileReplacements),
                body.applyReplacements(fileReplacements)
            )
        }
    }

    private fun Project.createSourceFile(baseDirectory: PsiDirectory,
                                         subDirectory: String,
                                         fileName: String,
                                         body: String) {
        val directory = try {
            DirectoryUtil.createSubdirectories(subDirectory, baseDirectory, File.separator)
        } catch (exception: IncorrectOperationException) {
            baseDirectory.findSubDirectory(subDirectory) ?: throw exception
        }

        val file = PsiFileFactory.getInstance(this).createFileFromText(
            fileName,
            JavaLanguage.INSTANCE,
            body
        )
        directory.add(file)
    }

    private fun PsiDirectory.findSubDirectory(path: String): PsiDirectory? {
        var directory: PsiDirectory = this
        path.split(File.separatorChar).forEach {
            directory = directory.findSubdirectory(it) ?: return null
        }
        return directory
    }
}
