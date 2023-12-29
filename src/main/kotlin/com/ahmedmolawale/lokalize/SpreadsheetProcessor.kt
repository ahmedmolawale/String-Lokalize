package com.ahmedmolawale.lokalize


import com.ahmedmolawale.lokalize.states.FileProcessState
import com.ahmedmolawale.lokalize.utils.androidSupportedLocales
import com.ahmedmolawale.lokalize.utils.isBlankOrEmpty
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream


private const val SHEET_INDEX = 0
private const val KEY_NAME = "key"
private const val RESOURCE_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"
private const val RESOURCE_SUFFIX = "</resources>"
private const val EXCEL_FILE_TYPE = "xls"
private const val X_EXCEL_FILE_TYPE = "xlsx"

class SpreadsheetProcessor(private val filePath: String) {

    fun execute(): FileProcessState {
        if (filePath.isBlankOrEmpty()) {
            return FileProcessState.Error("Please select a file.")
        }

        val file = File(filePath)
        val fileExtension = file.extension
        if (fileExtension != EXCEL_FILE_TYPE && fileExtension != X_EXCEL_FILE_TYPE) {
            return FileProcessState.Error("Only .xls and .xlsx files are allowed.")
        }

        val xExcelType = fileExtension == X_EXCEL_FILE_TYPE
        var inputStream: FileInputStream? = null
        return try {
            inputStream = FileInputStream(file)
            val workbook: Workbook = if (xExcelType) {
                XSSFWorkbook(inputStream)
            } else {
                HSSFWorkbook(inputStream)
            }
            processWorkbook(workbook)
        } catch (e: Exception) {
            FileProcessState.Error("Please select a valid spreadsheet file.")
        } finally {
            inputStream?.close()
        }
    }

    private fun processWorkbook(workbook: Workbook): FileProcessState {
        val sheet: Sheet = workbook.getSheetAt(SHEET_INDEX)
        val firstRowNum = sheet.firstRowNum
        val lastRowNum = sheet.lastRowNum

        val firstRow = sheet.getRow(firstRowNum)
        val firstCellNum = firstRow.firstCellNum
        val lastCellNum = firstRow.lastCellNum
        val firstCell = firstRow.getCell(firstCellNum.toInt())
        val stringCellValue = firstCell.stringCellValue.lowercase()
        if (stringCellValue != KEY_NAME) {
            return FileProcessState.Error(
                "The first column in the first row must be named \"key\".\n" +
                        "Please check the template file for reference."
            )
        }

        //save all the languages available in the spreadsheet
        val languages: MutableList<String> = mutableListOf()
        for (i in (firstCellNum + 1)..lastCellNum) { //plus because we wanna skip the key column
            val cell = firstRow.getCell(i) ?: break
            val cellValue = cell.stringCellValue
            if (!androidSupportedLocales.contains(cellValue)) {
                return FileProcessState.Error(
                    "The language with code $cellValue in column ${i+1} is " +
                            "not supported to be used as an Android resource locale.\n" +
                            "If you think this should be allowed. Please open an issue on the repository."
                )
            }
            languages.add(cellValue)
        }
        if (languages.isEmpty()) {
            return FileProcessState.Error(
                "The spreadsheet must have at least one language column." +
                        "\nPlease check the template file for reference."
            )
        }
        return createResources(languages, firstRowNum, lastRowNum, sheet)
    }

    private fun createResources(
        languages: List<String>,
        firstRowNum: Int,
        lastRowNum: Int,
        sheet: Sheet
    ): FileProcessState {
        val data = HashMap<String, StringBuilder>()
        for (rowNo in (firstRowNum + 1)..lastRowNum) {
            val row = sheet.getRow(rowNo) ?: break
            val keyName = row.getCell(0) ?: break
            languages.forEachIndexed { index, value ->
                data.putIfAbsent(value, StringBuilder().append(RESOURCE_PREFIX))
                val keyValue = row.getCell(index + 1) ?: return@forEachIndexed
                val formattedValue =
                    "<string name=\"${keyName.stringCellValue}\">${keyValue.stringCellValue}</string>\n"
                data[value]?.append(formattedValue)
            }
        }
        data.values.forEach {
            it.append(RESOURCE_SUFFIX)
        }
        return FileProcessState.Success(data)
    }
}
