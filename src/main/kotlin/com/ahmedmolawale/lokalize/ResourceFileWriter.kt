package com.ahmedmolawale.lokalize

import java.io.File
import java.io.FileWriter
import java.nio.file.Paths

private const val VALUE_DIR_PREFIX = "values"
private const val STRING_FILE_NAME = "strings.xml"

class ResourceFileWriter(private val data: Map<String, StringBuilder>, private val locationToSaveOutput: String) {

    fun createResourceFiles(): Int {
        var processed = 0
        for ((key, value) in data.entries) {
            val path = Paths.get(locationToSaveOutput, "$VALUE_DIR_PREFIX-$key", STRING_FILE_NAME)
            val stringFile = File(path.toString())
            if (stringFile.exists()) { //we have to deal with this case separatly.
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