package com.ahmedmolawale.lokalize

import com.ahmedmolawale.lokalize.states.FileProcessState
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class AppToolWindowContent {

    val contentPanel = JPanel()

    init {
        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS) //parent panel
        contentPanel.border = BorderFactory.createEmptyBorder(20, 20, 0, 0)
        contentPanel.add(createFirstPanel())
    }

    private fun createFirstPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        val descriptionLabel = JLabel()
        val fileChooserTextField = TextFieldWithBrowseButton(JTextField(50))
        val importButton = JButton("Import")

        descriptionLabel.text = "Please select a spreadsheet below to import"
        descriptionLabel.alignmentX = Component.LEFT_ALIGNMENT

        fileChooserTextField.addBrowseFolderListener(TextBrowseFolderListener(SpreadsheetFileChooserDescriptor()))
        fileChooserTextField.maximumSize = fileChooserTextField.preferredSize
        fileChooserTextField.alignmentX = Component.LEFT_ALIGNMENT

        importButton.alignmentX = Component.LEFT_ALIGNMENT
        importButton.addActionListener {
            val fileName = fileChooserTextField.textField.text
            val state = SpreadsheetProcessor(fileName).execute()
            displayState(state)
        }

        panel.add(descriptionLabel)
        panel.add(Box.createRigidArea(Dimension(0, 7)))
        panel.add(fileChooserTextField)
        panel.add(Box.createRigidArea(Dimension(0, 7)))
        panel.add(importButton)
        return panel
    }

    private fun displayState(state: FileProcessState) {
        when (state) {
            is FileProcessState.Success -> {
                Messages.showMessageDialog(
                    "Processed ${state.data.keys.count()} languages",
                    "Success",
                    Messages.getInformationIcon()
                )
            }
            is FileProcessState.Error -> {
                Messages.showMessageDialog(state.message, "Information", Messages.getErrorIcon())
            }
        }
    }
}
