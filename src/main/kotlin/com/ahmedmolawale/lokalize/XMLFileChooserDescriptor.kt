package com.ahmedmolawale.lokalize

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.vfs.VirtualFile

class XMLFileChooserDescriptor : FileChooserDescriptor(true, false, false, false, false, false) {

    override fun isFileSelectable(file: VirtualFile): Boolean {
        val fileTypeManager = FileTypeManager.getInstance()
        val xmlFileType: FileType = fileTypeManager.getFileTypeByExtension("xml")
        return file.fileType == xmlFileType
    }
}