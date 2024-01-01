package com.ahmedmolawale.lokalize


import com.ahmedmolawale.lokalize.states.XMLProcessState
import com.ahmedmolawale.lokalize.utils.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList


private const val XML_FILE_TYPE = "xml"
private const val STRING_TAG = "string"
private const val STRING_ATTRIBUTE = "name"

class XMLProcessor(
    private val fileHelper: FileHelper
) {

    fun execute(filePath: String, locationToSaveOutput: String): XMLProcessState {
        if (filePath.isBlankOrEmpty()) {
            return XMLProcessState.Error(NO_FILE_SELECTED)
        }
        if (locationToSaveOutput.isBlankOrEmpty() || !fileHelper.isDirectory(locationToSaveOutput)) {
            return XMLProcessState.Error(NO_VALID_DIRECTORY_SELECTED)
        }

        val fileExtension = fileHelper.getFileExtension(filePath)
        if (fileExtension != XML_FILE_TYPE) {
            return XMLProcessState.Error(INVALID_XML_FILE_FORMAT)
        }
        return try {
            processDocument(fileHelper.getXMLFileDocument(filePath))
        } catch (e: Exception) {
            XMLProcessState.Error(INVALID_SPREADSHEET)
        }
    }

    private fun processDocument(document: Document): XMLProcessState {
        val stringNodes: NodeList = document.getElementsByTagName(STRING_TAG)
        val data = HashMap<String, String>()
        for (i in 0 until stringNodes.length) {
            val node: Node = stringNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                data.putIfAbsent(element.getAttribute(STRING_ATTRIBUTE), element.textContent)
            }
        }
        return XMLProcessState.Success(data)
    }
}