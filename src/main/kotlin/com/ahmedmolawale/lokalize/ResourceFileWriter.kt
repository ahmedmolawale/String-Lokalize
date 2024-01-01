package com.ahmedmolawale.lokalize

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.nio.file.Paths

private const val VALUE_DIR_PREFIX = "values"
private const val STRING_FILE_NAME = "strings.xml"
private const val SPREADSHEET_FILE_NAME = "strings.xlsx"
private const val SHEET_NAME = "data"
private const val KEY_NAME = "key"
private const val KEY_VALUE_NAME = "value"
private const val FIRST_COLUMN = 0
private const val SECOND_COLUMN = 1

class ResourceFileWriter(
    private val locationToSaveOutput: String
) {

    fun createStringResources(data: Map<String, StringBuilder>): Int {
        var processed = 0
        for ((key, value) in data.entries) {
            val path = Paths.get(locationToSaveOutput, "$VALUE_DIR_PREFIX-$key", STRING_FILE_NAME)
            val stringFile = File(path.toString())
            if (stringFile.exists()) { //we have to deal with this case separately.
                continue
            }

            //check if the directory exist
            val directoryPath = Paths.get(locationToSaveOutput, "$VALUE_DIR_PREFIX-$key")
            val directory = File(directoryPath.toString())
            if (!directory.exists()) {
                directory.mkdir()
            }
            //now, we can create the file
            stringFile.createNewFile()
            if (writeToFile(stringFile, value.toString())) {
                processed++
            }
        }
        return processed
    }

    fun createSpreadsheet(data: Map<String, String>): String {
        val workbook: Workbook = XSSFWorkbook()
        val headerCellStyle = getHeaderFontStyle(workbook)
        val sheet: Sheet = workbook.createSheet(SHEET_NAME)
        sheet.createRow(0).apply {
            val cell1 = createCell(FIRST_COLUMN)
            cell1.setCellValue(KEY_NAME)
            cell1.cellStyle = headerCellStyle
            val cell2 = createCell(SECOND_COLUMN)
            cell2.setCellValue(KEY_VALUE_NAME)
            cell2.cellStyle = headerCellStyle
        }

        data.entries.forEachIndexed { index, entry ->
            val row = sheet.createRow(index + 1)
            val cell1 = row.createCell(FIRST_COLUMN)
            cell1.setCellValue(entry.key)
            val cell2 = row.createCell(SECOND_COLUMN)
            cell2.setCellValue(entry.value)
        }
        sheet.autoSizeColumn(FIRST_COLUMN)
        sheet.autoSizeColumn(SECOND_COLUMN)
        return try {
            val path = Paths.get(locationToSaveOutput, SPREADSHEET_FILE_NAME)
            val xlsx = File(path.toString())
            xlsx.createNewFile()
            val fileOutputStream = FileOutputStream(xlsx)
            workbook.write(fileOutputStream)
            fileOutputStream.close()
            workbook.close()
            "Spreadsheet file created successfully."
        } catch (e: Exception) {
            e.printStackTrace()
            "An error occurred while creating the Spreadsheet file."
        }
    }

    private fun getHeaderFontStyle(workbook: Workbook): CellStyle {
        val cellStyle = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 14
        font.color = IndexedColors.BLACK.index
        cellStyle.setFont(font)
        return cellStyle
    }

    private fun writeToFile(file: File, xmlSource: String): Boolean {
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