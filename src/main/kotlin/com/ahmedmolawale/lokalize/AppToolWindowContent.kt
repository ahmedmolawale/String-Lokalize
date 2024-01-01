package com.ahmedmolawale.lokalize

import com.ahmedmolawale.lokalize.components.LineComponent
import com.ahmedmolawale.lokalize.states.SpreadsheetProcessState
import com.ahmedmolawale.lokalize.states.XMLProcessState
import com.ahmedmolawale.lokalize.utils.FileHelper
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class AppToolWindowContent {

    val contentPanel = JPanel()
    private lateinit var locationToSaveOutput: String

    init {
        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS) //parent panel
        contentPanel.border = BorderFactory.createEmptyBorder(20, 12, 0, 12)
        contentPanel.add(createImportPanel())
        contentPanel.add(Box.createRigidArea(Dimension(0, 20)))
        contentPanel.add(createExportPanel())
    }

    private fun createImportPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
//        val headerLabel = JLabel(AllIcons.General.DropdownGutter)
        val headerLabel = JLabel()
        val spreadsheetLabel = JLabel()
        val spreadsheetFileChooserTextField = TextFieldWithBrowseButton(JTextField(50))
        val locationToSaveLabel = JLabel()
        val locationToSaveChooserTextField = TextFieldWithBrowseButton(JTextField(50))
        val importButton = JButton("Import")

        headerLabel.text = "<> Import from Spreadsheet"
        headerLabel.alignmentX = Component.LEFT_ALIGNMENT

        spreadsheetLabel.text = "Please select a spreadsheet below to import"
        spreadsheetLabel.alignmentX = Component.LEFT_ALIGNMENT

        spreadsheetFileChooserTextField.addBrowseFolderListener(
            TextBrowseFolderListener(
                SpreadsheetFileChooserDescriptor()
            )
        )
        spreadsheetFileChooserTextField.maximumSize = spreadsheetFileChooserTextField.preferredSize
        spreadsheetFileChooserTextField.alignmentX = Component.LEFT_ALIGNMENT

        locationToSaveLabel.text = "Please select the directory to save the output"
        locationToSaveLabel.alignmentX = Component.LEFT_ALIGNMENT

        locationToSaveChooserTextField.addBrowseFolderListener(
            TextBrowseFolderListener(
                FileChooserDescriptor(
                    false,
                    true,
                    false,
                    false,
                    false,
                    false
                )
            )
        )
        locationToSaveChooserTextField.maximumSize = spreadsheetFileChooserTextField.preferredSize
        locationToSaveChooserTextField.alignmentX = Component.LEFT_ALIGNMENT

        importButton.alignmentX = Component.LEFT_ALIGNMENT
        importButton.addActionListener {
            val fileName = spreadsheetFileChooserTextField.textField.text
            locationToSaveOutput = locationToSaveChooserTextField.textField.text
            val state = SpreadsheetProcessor(FileHelper()).execute(fileName, locationToSaveOutput)
            displayState(state)
        }

        panel.add(headerLabel)
        panel.add(Box.createRigidArea(Dimension(0, 15)))
        panel.add(spreadsheetLabel)
        panel.add(Box.createRigidArea(Dimension(0, 7)))
        panel.add(spreadsheetFileChooserTextField)
        panel.add(Box.createRigidArea(Dimension(0, 10)))
        panel.add(locationToSaveLabel)
        panel.add(Box.createRigidArea(Dimension(0, 7)))
        panel.add(locationToSaveChooserTextField)
        panel.add(Box.createRigidArea(Dimension(0, 10)))
        panel.add(importButton)
        return panel
    }

    private fun createExportPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
//        val headerLabel = JLabel(AllIcons.General.DropdownGutter)
        val headerLabel = JLabel()
        val xmlLabel = JLabel()
        val xmlFileChooserTextField = TextFieldWithBrowseButton(JTextField(50))
        val locationToSaveLabel = JLabel()
        val locationToSaveChooserTextField = TextFieldWithBrowseButton(JTextField(50))
        val importButton = JButton("Export")

        headerLabel.text = "<> Export to Spreadsheet"
        headerLabel.alignmentX = Component.LEFT_ALIGNMENT

        xmlLabel.text = "Please select the string resource file to export"
        xmlLabel.alignmentX = Component.LEFT_ALIGNMENT

        xmlFileChooserTextField.addBrowseFolderListener(
            TextBrowseFolderListener(
                XMLFileChooserDescriptor()
            )
        )
        xmlFileChooserTextField.maximumSize = xmlFileChooserTextField.preferredSize
        xmlFileChooserTextField.alignmentX = Component.LEFT_ALIGNMENT

        locationToSaveLabel.text = "Please select the directory to save the output"
        locationToSaveLabel.alignmentX = Component.LEFT_ALIGNMENT

        locationToSaveChooserTextField.addBrowseFolderListener(
            TextBrowseFolderListener(
                FileChooserDescriptor(
                    false,
                    true,
                    false,
                    false,
                    false,
                    false
                )
            )
        )
        locationToSaveChooserTextField.maximumSize = xmlFileChooserTextField.preferredSize
        locationToSaveChooserTextField.alignmentX = Component.LEFT_ALIGNMENT

        importButton.alignmentX = Component.LEFT_ALIGNMENT
        importButton.addActionListener {
            val fileName = xmlFileChooserTextField.textField.text
            locationToSaveOutput = locationToSaveChooserTextField.textField.text
            val state = XMLProcessor(FileHelper()).execute(fileName, locationToSaveOutput)
            displayState(state)
        }

        panel.add(LineComponent())
        panel.add(Box.createRigidArea(Dimension(0, 20)))
        panel.add(headerLabel)
        panel.add(Box.createRigidArea(Dimension(0, 15)))
        panel.add(xmlLabel)
        panel.add(Box.createRigidArea(Dimension(0, 7)))
        panel.add(xmlFileChooserTextField)
        panel.add(Box.createRigidArea(Dimension(0, 10)))
        panel.add(locationToSaveLabel)
        panel.add(Box.createRigidArea(Dimension(0, 7)))
        panel.add(locationToSaveChooserTextField)
        panel.add(Box.createRigidArea(Dimension(0, 10)))
        panel.add(importButton)
        panel.maximumSize = panel.preferredSize
        return panel
    }

    private fun displayState(state: SpreadsheetProcessState) {
        when (state) {
            is SpreadsheetProcessState.Success -> {
                val processed = ResourceFileWriter(locationToSaveOutput).createStringResources(state.data)
                Messages.showMessageDialog(
                    "$processed out of ${state.data.keys.count()} languages has been processed successfully.",
                    "Success",
                    Messages.getInformationIcon()
                )
            }
            is SpreadsheetProcessState.Error -> {
                Messages.showMessageDialog(state.message, "Information", Messages.getErrorIcon())
            }
        }
    }

    private fun displayState(state: XMLProcessState) {
        when (state) {
            is XMLProcessState.Success -> {
                val result = ResourceFileWriter(locationToSaveOutput).createSpreadsheet(state.data)
                Messages.showMessageDialog(
                    result,
                    "Success",
                    Messages.getInformationIcon()
                )
            }
            is XMLProcessState.Error -> {
                Messages.showMessageDialog(state.message, "Information", Messages.getErrorIcon())
            }
        }
    }
}
