package com.badoo.mobile.plugin.action

import com.badoo.mobile.plugin.action.dialog.GenerateRibDialog
import com.badoo.mobile.plugin.generator.TemplateWriter
import com.badoo.mobile.plugin.generator.createReplacements
import com.badoo.mobile.plugin.template.JavaResourceProvider
import com.badoo.mobile.plugin.template.MetaInformationProvider
import com.badoo.mobile.plugin.template.ResourceProvider
import com.badoo.mobile.plugin.template.TemplateFilesProvider
import com.intellij.facet.FacetManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper
import com.intellij.refactoring.PackageWrapper
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes

open class GenerateTemplateAction : AnAction(), GenerateRibDialog.Listener {

    private val resourceProvider = JavaResourceProvider()
    private val metaInformationProvider = MetaInformationProvider(resourceProvider)
    private val filesProvider = TemplateFilesProvider(resourceProvider, metaInformationProvider)

    private lateinit var dataContext: DataContext

    override fun update(event: AnActionEvent) {
        dataContext = event.dataContext

        val presentation = event.presentation
        val enabled = isAvailable(event.dataContext)
        presentation.isVisible = enabled
        presentation.isEnabled = enabled
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val dialog = GenerateRibDialog(this, metaInformationProvider.templates)
        dialog.show()
    }

    override fun onGenerateClicked(tokenValues: Map<String, String>, templateId: String) {
        val project = CommonDataKeys.PROJECT.getData(dataContext)!!
        val view = LangDataKeys.IDE_VIEW.getData(dataContext)!!
        val directory = view.orChooseDirectory!!
        val currentModule = ModuleUtilCore.findModuleForPsiElement(directory)!!
        val androidFacet = FacetManager.getInstance(currentModule).getFacetByType(AndroidFacet.ID)!!

        val targetPackage = PackageWrapper(PsiManager.getInstance(project), getCurrentPackageName())
        val targetModulePackage = androidFacet.manifest?.getPackage()?.value!!

        val template = metaInformationProvider.templates.find { it.id == templateId }!!

        val sourceSetDirectories = SourceSetDirectoriesProvider(project, androidFacet, directory)

        val replacements = createReplacements(
            template.sourcePackage,
            targetPackage.qualifiedName,
            template.modulePackage,
            targetModulePackage,
            template.tokens.map { it.id to it }.toMap(),
            tokenValues
        )

        val writer = TemplateWriter(filesProvider.templateFiles.getValue(templateId))

        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().executeCommand(project, {
                writer.writeFiles(project, sourceSetDirectories, replacements)
            }, "Generate new RIB", null)
        }
    }

    private fun isAvailable(dataContext: DataContext): Boolean {
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return false

        val view = LangDataKeys.IDE_VIEW.getData(dataContext)
        if (view == null || view.directories.isEmpty()) {
            return false
        }

        val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
        for (dir in view.directories) {
            if (projectFileIndex.isUnderSourceRootOfType(dir.virtualFile, JavaModuleSourceRootTypes.SOURCES) && checkPackageExists(dir)) {
                return true
            }
        }

        return false
    }

    private fun getCurrentPackageName(): String {
        val view = LangDataKeys.IDE_VIEW.getData(dataContext)!!
        val directory = view.orChooseDirectory!!
        val psiPackage = JavaDirectoryService.getInstance().getPackage(directory)
        return psiPackage!!.qualifiedName
    }

    private fun checkPackageExists(directory: PsiDirectory): Boolean {
        val pkg = JavaDirectoryService.getInstance().getPackage(directory) ?: return false
        val name = pkg.qualifiedName
        return StringUtil.isEmpty(name) || PsiNameHelper.getInstance(directory.project).isQualifiedName(name)
    }
}
