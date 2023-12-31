package com.ahmedmolawale.lokalize


import com.ahmedmolawale.lokalize.states.FileProcessState
import com.ahmedmolawale.lokalize.utils.FileHelper
import com.ahmedmolawale.lokalize.utils.androidSupportedLocales
import com.ahmedmolawale.lokalize.utils.isBlankOrEmpty
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

const val NO_FILE_SELECTED = "Please select a file."
const val NO_VALID_DIRECTORY_SELECTED = "Please select a valid directory to save the output."
const val INVALID_FILE_FORMAT = "Only .xls and .xlsx files are allowed."
const val INVALID_SPREADSHEET = "Please select a valid spreadsheet file."
const val INVALID_KEY_ON_SPREADSHEET = "The first column in the first row must be named \"key\".\n" +
        "Please check the template file for reference."
const val NO_LANGUAGE_COLUMN = "The spreadsheet must have at least one language column." +
        "\nPlease check the template file for reference."
const val INVALID_LANGUAGE_COLUMN = "The language with code %s in column %d is " +
        "not supported to be used as an Android resource locale.\n" +
        "If you think this should be allowed. Please open an issue on the repository."


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

    fun execute(filePath: String, locationToSaveOutput: String): FileProcessState {
        if (filePath.isBlankOrEmpty()) {
            return FileProcessState.Error(NO_FILE_SELECTED)
        }
        if (locationToSaveOutput.isBlankOrEmpty() || !fileHelper.isDirectory(locationToSaveOutput)) {
            return FileProcessState.Error(NO_VALID_DIRECTORY_SELECTED)
        }

        val fileExtension = fileHelper.getFileExtension(filePath)
        if (fileExtension != EXCEL_FILE_TYPE && fileExtension != X_EXCEL_FILE_TYPE) {
            return FileProcessState.Error(INVALID_FILE_FORMAT)
        }
        val xExcelType = fileExtension == X_EXCEL_FILE_TYPE
        return try {
            processWorkbook(fileHelper.getFileWorkbook(filePath, xExcelType))
        } catch (e: Exception) {
            FileProcessState.Error(INVALID_SPREADSHEET)
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
            return FileProcessState.Error(INVALID_KEY_ON_SPREADSHEET)
        }

        //save all the languages available in the spreadsheet
        val languages: MutableList<String> = mutableListOf()
        for (i in (firstCellNum + 1)..lastCellNum) { //plus because we wanna skip the key column
            val cell = firstRow.getCell(i) ?: break
            val cellValue = cell.stringCellValue
            if (!androidSupportedLocales.contains(cellValue)) {
                return FileProcessState.Error(
                    String.format(INVALID_LANGUAGE_COLUMN, cellValue, i + 1)
                )
            }
            languages.add(cellValue)
        }
        if (languages.isEmpty()) {
            return FileProcessState.Error(NO_LANGUAGE_COLUMN)
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
                    "$STRING_ELEMENT_PREFIX\"${keyName.stringCellValue}\"$STRING_ELEMENT_PREFIX_END" +
                            "${keyValue.stringCellValue}$STRING_ELEMENT_SUFFIX\n"
                data[value]?.append(formattedValue)
            }
        }
        data.values.forEach {
            it.append(RESOURCE_SUFFIX)
        }
        return FileProcessState.Success(data)
    }
}
