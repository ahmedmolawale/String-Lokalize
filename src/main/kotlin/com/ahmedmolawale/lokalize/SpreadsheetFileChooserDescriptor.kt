package com.ahmedmolawale.lokalize

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.vfs.VirtualFile

class SpreadsheetFileChooserDescriptor : FileChooserDescriptor(true, false, false, false, false, false) {

    override fun isFileSelectable(file: VirtualFile): Boolean {
        val fileTypeManager = FileTypeManager.getInstance()
        val excelFileType: FileType = fileTypeManager.getFileTypeByExtension("xls")
        val excelFileTypeXlsx: FileType = fileTypeManager.getFileTypeByExtension("xlsx")
        return file.fileType == excelFileType || file.fileType == excelFileTypeXlsx
    }
}