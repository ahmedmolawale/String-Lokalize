package com.ahmedmolawale.lokalize

import com.ahmedmolawale.lokalize.states.SpreadsheetProcessState
import com.ahmedmolawale.lokalize.utils.*
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SpreadsheetProcessorTest {

    private var filePath: String = "/usr/file.xls"
    private var locationToSaveOutput: String = "/usr/save"
    private val fileHelper: FileHelper = mockk()
    private val sut: SpreadsheetProcessor = SpreadsheetProcessor(fileHelper)

    @Before
    fun setUp() {
        every { fileHelper.isDirectory(locationToSaveOutput) } returns true
        every { fileHelper.getFileExtension(filePath) } returns "xlsx"
    }

    @Test
    fun `GIVEN empty spreadsheet file name WHEN execute() is called THEN return Error State`() {
        filePath = ""

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        assertThat(result.message).isEqualTo(NO_FILE_SELECTED)
    }

    @Test
    fun `GIVEN invalid output directory WHEN execute() is called THEN return Error State`() {
        locationToSaveOutput = "invalid_"
        every { fileHelper.isDirectory(locationToSaveOutput) } returns false

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        assertThat(result.message).isEqualTo(NO_VALID_DIRECTORY_SELECTED)
    }

    @Test
    fun `GIVEN invalid file format WHEN execute() is called THEN return Error State`() {
        every { fileHelper.getFileExtension(filePath) } returns "pdf"

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        assertThat(result.message).isEqualTo(INVALID_SPREADSHEET_FILE_FORMAT)
    }

    @Test
    fun `GIVEN file not valid WHEN execute() is called THEN return throws an exception`() {
        every { fileHelper.getFileWorkbook(filePath, any()) } throws IOException("")

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        assertThat(result.message).isEqualTo(INVALID_SPREADSHEET)
    }

    @Test
    fun `GIVEN workbook with invalid key cell WHEN execute() is called THEN returns appropriate error`() {
        val workbook: Workbook = createWorkbook("invalid_key")
        every { fileHelper.getFileWorkbook(filePath, any()) } returns workbook

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        assertThat(result.message).isEqualTo(INVALID_KEY_ON_SPREADSHEET)
    }

    @Test
    fun `GIVEN workbook with no language column WHEN execute() is called THEN returns appropriate error`() {
        val workbook: Workbook = createWorkbook(isLanguageAvailable = false)
        every { fileHelper.getFileWorkbook(filePath, any()) } returns workbook

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        assertThat(result.message).isEqualTo(NO_LANGUAGE_COLUMN_ON_SPREADSHEET)
    }

    @Test
    fun `GIVEN workbook with invalid language code column WHEN execute() is called THEN returns appropriate error`() {
        val language = "123"
        val workbook: Workbook = createWorkbook(language = language)
        every { fileHelper.getFileWorkbook(filePath, any()) } returns workbook

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Error::class.java)
        result as SpreadsheetProcessState.Error
        val expected = String.format(INVALID_LANGUAGE_COLUMN_ON_SPREADSHEET, language, 2)
        assertThat(result.message).isEqualTo(expected)
    }

    @Test
    fun `GIVEN workbook with valid data WHEN execute() is called THEN returns correct data`() {
        val workbook: Workbook = createWorkbook()
        every { fileHelper.getFileWorkbook(filePath, any()) } returns workbook

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(SpreadsheetProcessState.Success::class.java)
        result as SpreadsheetProcessState.Success
        val expected = hashMapOf(
            "en" to StringBuilder().apply {
                append(
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<resources>\n"
                )
                append("<string name=\"config_name\">olawale</string>\n")
                append("</resources>")
            }
        )
        assertThat(result.data.keys.size).isEqualTo(expected.keys.size)
        assertThat(result.data.keys.first()).isEqualTo(expected.keys.first())
        assertThat(result.data.values.first().toString()).isEqualTo(expected.values.first().toString())
    }

    private fun createWorkbook(
        headerRowKeyName: String = "key",
        language: String = "en",
        isLanguageAvailable: Boolean = true
    ): Workbook {
        val workbook: Workbook = HSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("data")
        sheet.createRow(0).apply {//header row
            createCell(0).setCellValue(headerRowKeyName)
            if (isLanguageAvailable)
                createCell(1).setCellValue(language)
        }
        sheet.createRow(1).apply {//key value
            createCell(0).setCellValue("config_name")
            createCell(1).setCellValue("olawale")
        }
        return workbook
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}