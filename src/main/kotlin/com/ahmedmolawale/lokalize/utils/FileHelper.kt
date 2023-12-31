package com.ahmedmolawale.lokalize.utils

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import javax.xml.parsers.DocumentBuilderFactory

class FileHelper {
    fun isDirectory(path: String): Boolean {
        return File(path).isDirectory
    }

    fun getFileExtension(path: String): String {
        return File(path).extension
    }

    @Throws(Exception::class)
    fun getFileWorkbook(path: String, excelType: Boolean): Workbook {
        val inputStream = FileInputStream(File(path))
        val workbook = if (excelType) {
            XSSFWorkbook(inputStream)
        } else {
            HSSFWorkbook(inputStream)
        }
        inputStream.close()
        return workbook
    }

    @Throws(Exception::class)
    fun getXMLFileDocument(path: String): Document {
        val file = File(path)
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(file)
        document.documentElement.normalize()
        return document
    }

    fun writeToFile(file: File, xmlSource: String): Boolean {
        return try {
            val fw = FileWriter(file)
            fw.write(xmlSource)
            fw.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
