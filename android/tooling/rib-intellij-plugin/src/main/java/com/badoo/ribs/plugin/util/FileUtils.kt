package com.badoo.ribs.plugin.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import java.io.File


fun VirtualFile.toPsiDirectory(project: Project): PsiDirectory? {
    return PsiManager.getInstance(project).findDirectory(this)!!
}


fun File.toPsiDirectory(project: Project): PsiDirectory? {
    val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(this)!!
    return virtualFile.toPsiDirectory(project)
}