package com.ahmedmolawale.lokalize

import com.ahmedmolawale.lokalize.states.XMLProcessState
import com.ahmedmolawale.lokalize.utils.*
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test


class XMLProcessorTest {

    private var filePath: String = "/usr/file.xml"
    private var locationToSaveOutput: String = "/usr/save"
    private val fileHelper: FileHelper = mockk(relaxed = true)
    private val sut = XMLProcessor(fileHelper)

    @Before
    fun setUp() {
        every { fileHelper.isDirectory(locationToSaveOutput) } returns true
        every { fileHelper.getFileExtension(filePath) } returns "xml"
    }

    @Test
    fun `GIVEN empty spreadsheet file name WHEN execute() is called THEN return Error State`() {
        filePath = ""

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(XMLProcessState.Error::class.java)
        result as XMLProcessState.Error
        assertThat(result.message).isEqualTo(NO_FILE_SELECTED)
    }

    @Test
    fun `GIVEN invalid output directory WHEN execute() is called THEN return Error State`() {
        locationToSaveOutput = "invalid_"
        every { fileHelper.isDirectory(locationToSaveOutput) } returns false

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(XMLProcessState.Error::class.java)
        result as XMLProcessState.Error
        assertThat(result.message).isEqualTo(NO_VALID_DIRECTORY_SELECTED)
    }

    @Test
    fun `GIVEN invalid file format WHEN execute() is called THEN return Error State`() {
        every { fileHelper.getFileExtension(filePath) } returns "pdf"

        val result = sut.execute(filePath, locationToSaveOutput)

        assertThat(result).isInstanceOf(XMLProcessState.Error::class.java)
        result as XMLProcessState.Error
        assertThat(result.message).isEqualTo(INVALID_XML_FILE_FORMAT)
    }
}