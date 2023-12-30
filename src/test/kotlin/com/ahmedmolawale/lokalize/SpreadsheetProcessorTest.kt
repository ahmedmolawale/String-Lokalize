package com.ahmedmolawale.lokalize

import com.ahmedmolawale.lokalize.states.FileProcessState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
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

        assertThat(result).isInstanceOf(FileProcessState.Error::class.java)
        result as FileProcessState.Error
        assertThat(result.message).isEqualTo(NO_FILE_SELECTED)
    }

    @Test
    fun `GIVEN invalid output directory WHEN execute() is called THEN return Error State`() {
        locationToSaveOutput = "invalid_"
        every { fileHelper.isDirectory(locationToSaveOutput) } returns false

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(FileProcessState.Error::class.java)
        result as FileProcessState.Error
        assertThat(result.message).isEqualTo(NO_VALID_DIRECTORY_SELECTED)
    }

    @Test
    fun `GIVEN invalid file format WHEN execute() is called THEN return Error State`() {
        every { fileHelper.getFileExtension(filePath) } returns "pdf"

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(FileProcessState.Error::class.java)
        result as FileProcessState.Error
        assertThat(result.message).isEqualTo(INVALID_FILE_FORMAT)
    }

    @Test
    fun `GIVEN file not valid WHEN execute() is called THEN return throws an exception`() {
        every { fileHelper.getFileWorkbook(filePath, any()) } throws IOException("")

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(FileProcessState.Error::class.java)
        result as FileProcessState.Error
        assertThat(result.message).isEqualTo(INVALID_SPREADSHEET)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}