package com.ahmedmolawale.lokalize


import com.ahmedmolawale.lokalize.states.SpreadsheetProcessState
import com.ahmedmolawale.lokalize.utils.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook


private const val SHEET_INDEX = 0
private const val KEY_NAME = "key"
private const val RESOURCE_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"
private const val RESOURCE_SUFFIX = "</resources>"
private const val EXCEL_FILE_TYPE = "xls"
private const val X_EXCEL_FILE_TYPE = "xlsx"
private const val STRING_ELEMENT_PREFIX = "<string name="
private const val STRING_ELEMENT_PREFIX_END = ">"
private const val STRING_ELEMENT_SUFFIX = "</string>"

class SpreadsheetProcessor(
    private val fileHelper: FileHelper
) {

    fun execute(filePath: String, locationToSaveOutput: String): SpreadsheetProcessState {
        if (filePath.isBlankOrEmpty()) {
            return SpreadsheetProcessState.Error(NO_FILE_SELECTED)
        }
        if (locationToSaveOutput.isBlankOrEmpty() || !fileHelper.isDirectory(locationToSaveOutput)) {
            return SpreadsheetProcessState.Error(NO_VALID_DIRECTORY_SELECTED)
        }

        val fileExtension = fileHelper.getFileExtension(filePath)
        if (fileExtension != EXCEL_FILE_TYPE && fileExtension != X_EXCEL_FILE_TYPE) {
            return SpreadsheetProcessState.Error(INVALID_SPREADSHEET_FILE_FORMAT)
        }
        val xExcelType = fileExtension == X_EXCEL_FILE_TYPE
        return try {
            processWorkbook(fileHelper.getFileWorkbook(filePath, xExcelType))
        } catch (e: Exception) {
            SpreadsheetProcessState.Error(INVALID_SPREADSHEET)
        }
    }

    private fun processWorkbook(workbook: Workbook): SpreadsheetProcessState {
        val sheet: Sheet = workbook.getSheetAt(SHEET_INDEX)
        val firstRowNum = sheet.firstRowNum
        val lastRowNum = sheet.lastRowNum

        val firstRow = sheet.getRow(firstRowNum)
        val firstCellNum = firstRow.firstCellNum
        val lastCellNum = firstRow.lastCellNum
        val firstCell = firstRow.getCell(firstCellNum.toInt())
        val stringCellValue = firstCell.stringCellValue.lowercase()
        if (stringCellValue != KEY_NAME) {
            return SpreadsheetProcessState.Error(INVALID_KEY_ON_SPREADSHEET)
        }

        //save all the languages available in the spreadsheet
        val languages: MutableList<String> = mutableListOf()
        for (i in (firstCellNum + 1)..lastCellNum) { //plus because we wanna skip the key column
            val cell = firstRow.getCell(i) ?: break
            val cellValue = cell.stringCellValue
            if (!androidSupportedLocales.contains(cellValue)) {
                return SpreadsheetProcessState.Error(
                    String.format(INVALID_LANGUAGE_COLUMN_ON_SPREADSHEET, cellValue, i + 1)
                )
            }
            languages.add(cellValue)
        }
        if (languages.isEmpty()) {
            return SpreadsheetProcessState.Error(NO_LANGUAGE_COLUMN_ON_SPREADSHEET)
        }
        return createResources(languages, firstRowNum, lastRowNum, sheet)
    }

    private fun createResources(
        languages: List<String>,
        firstRowNum: Int,
        lastRowNum: Int,
        sheet: Sheet
    ): SpreadsheetProcessState {
        val data = HashMap<String, StringBuilder>()
        for (rowNo in (firstRowNum + 1)..lastRowNum) {
            val row = sheet.getRow(rowNo) ?: break
            val keyName = row.getCell(0) ?: break
            languages.forEachIndexed { index, value ->
                data.putIfAbsent(value, StringBuilder().append(RESOURCE_PREFIX))
                val keyValue = row.getCell(index + 1) ?: return@forEachIndexed
                val formattedValue =
                    "$STRING_ELEMENT_PREFIX\"${keyName.stringCellValue}\"$STRING_ELEMENT_PREFIX_END" +
                            "${keyValue.stringCellValue}$STRING_ELEMENT_SUFFIX\n"
                data[value]?.append(formattedValue)
            }
        }
        data.values.forEach {
            it.append(RESOURCE_SUFFIX)
        }
        return SpreadsheetProcessState.Success(data)
    }
}
