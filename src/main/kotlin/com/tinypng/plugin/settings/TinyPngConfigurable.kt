/*
 * Copyright 2025 Hugo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tinypng.plugin.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.JBUI
import com.tinypng.plugin.models.ResizeMethod
import com.tinypng.plugin.services.TinyPngService
import java.awt.BorderLayout
import javax.swing.*

/**
 * Settings page for TinyPNG plugin configuration.
 */
class TinyPngConfigurable : Configurable {
    
    private var panel: DialogPanel? = null
    private lateinit var apiKeyListModel: DefaultListModel<String>
    private lateinit var apiKeyList: JBList<String>
    private lateinit var autoSwitchCheckbox: JBCheckBox
    private lateinit var showNotificationsCheckbox: JBCheckBox
    private lateinit var preserveOriginalCheckbox: JBCheckBox
    private lateinit var resizeMethodCombo: JComboBox<ResizeMethod>
    private lateinit var compressionCountLabel: JBLabel
    
    private val settings = TinyPngSettings.getInstance()

    override fun getDisplayName(): String = "TinyPNG"

    override fun createComponent(): JComponent {
        apiKeyListModel = DefaultListModel<String>().apply {
            settings.apiKeys.forEach { addElement(it) }
        }
        
        apiKeyList = JBList(apiKeyListModel).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            cellRenderer = ApiKeyCellRenderer(settings)
        }
        
        autoSwitchCheckbox = JBCheckBox("Auto-switch to next key when limit reached", settings.autoSwitchKey)
        showNotificationsCheckbox = JBCheckBox("Show notifications after compression", settings.showNotifications)
        preserveOriginalCheckbox = JBCheckBox("Preserve original files (create .backup)", settings.preserveOriginalFiles)
        resizeMethodCombo = JComboBox(ResizeMethod.entries.toTypedArray()).apply {
            selectedItem = settings.defaultResizeMethod
            renderer = ResizeMethodRenderer()
        }
        compressionCountLabel = JBLabel("Compression count: Loading...")
        
        panel = panel {
            group("API Keys") {
                row {
                    val decorator = ToolbarDecorator.createDecorator(apiKeyList)
                        .setAddAction { addApiKey() }
                        .setRemoveAction { removeSelectedApiKey() }
                        .setMoveUpAction { moveApiKeyUp() }
                        .setMoveDownAction { moveApiKeyDown() }
                        .disableUpDownActions()
                    
                    val listPanel = decorator.createPanel().apply {
                        preferredSize = JBUI.size(400, 120)
                    }
                    cell(listPanel)
                        .align(AlignX.FILL)
                }
                row {
                    cell(autoSwitchCheckbox)
                }
            }
            
            group("API Status") {
                row {
                    cell(compressionCountLabel)
                }
                row {
                    button("Validate Key") { validateCurrentKey() }
                    link("Get API Key from tinypng.com") {
                        java.awt.Desktop.getDesktop().browse(java.net.URI("https://tinypng.com/developers"))
                    }
                }
            }
            
            group("Options") {
                row {
                    cell(showNotificationsCheckbox)
                }
                row {
                    cell(preserveOriginalCheckbox)
                }
                row("Default Resize Method:") {
                    cell(resizeMethodCombo)
                }
            }
        }
        
        // Load compression count in background
        loadCompressionCount()
        
        return panel!!
    }
    
    private fun addApiKey() {
        val key = JOptionPane.showInputDialog(
            panel,
            "Enter TinyPNG API Key:",
            "Add API Key",
            JOptionPane.PLAIN_MESSAGE
        )
        if (!key.isNullOrBlank()) {
            val trimmedKey = key.trim()
            if (!apiKeyListModel.contains(trimmedKey)) {
                apiKeyListModel.addElement(trimmedKey)
            }
        }
    }
    
    private fun removeSelectedApiKey() {
        val selectedIndex = apiKeyList.selectedIndex
        if (selectedIndex >= 0) {
            apiKeyListModel.removeElementAt(selectedIndex)
        }
    }
    
    private fun moveApiKeyUp() {
        val selectedIndex = apiKeyList.selectedIndex
        if (selectedIndex > 0) {
            val key = apiKeyListModel.getElementAt(selectedIndex)
            apiKeyListModel.removeElementAt(selectedIndex)
            apiKeyListModel.insertElementAt(key, selectedIndex - 1)
            apiKeyList.selectedIndex = selectedIndex - 1
        }
    }
    
    private fun moveApiKeyDown() {
        val selectedIndex = apiKeyList.selectedIndex
        if (selectedIndex >= 0 && selectedIndex < apiKeyListModel.size() - 1) {
            val key = apiKeyListModel.getElementAt(selectedIndex)
            apiKeyListModel.removeElementAt(selectedIndex)
            apiKeyListModel.insertElementAt(key, selectedIndex + 1)
            apiKeyList.selectedIndex = selectedIndex + 1
        }
    }
    
    private fun validateCurrentKey() {
        val key = if (apiKeyListModel.size() > 0) {
            apiKeyListModel.getElementAt(apiKeyList.selectedIndex.coerceAtLeast(0))
        } else {
            JOptionPane.showMessageDialog(
                panel,
                "No API keys configured.",
                "Validation",
                JOptionPane.WARNING_MESSAGE
            )
            return
        }
        
        compressionCountLabel.text = "Validating..."
        
        Thread {
            try {
                val service = TinyPngService()
                val isValid = service.validateKey(key)
                
                SwingUtilities.invokeLater {
                    if (isValid) {
                        val count = service.getCompressionCount()
                        compressionCountLabel.text = "Compression count this month: $count / 500"
                        JOptionPane.showMessageDialog(
                            panel,
                            "API key is valid!\nCompressions this month: $count",
                            "Validation Successful",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    } else {
                        compressionCountLabel.text = "Invalid API key"
                        JOptionPane.showMessageDialog(
                            panel,
                            "API key is invalid!",
                            "Validation Failed",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    compressionCountLabel.text = "Error validating key"
                    JOptionPane.showMessageDialog(
                        panel,
                        "Error validating key: ${e.message}",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }.start()
    }
    
    private fun loadCompressionCount() {
        Thread {
            try {
                val key = settings.getCurrentApiKey()
                if (key != null) {
                    val service = TinyPngService()
                    if (service.validateKey(key)) {
                        val count = service.getCompressionCount()
                        SwingUtilities.invokeLater {
                            compressionCountLabel.text = "Compression count this month: $count / 500"
                        }
                    } else {
                        SwingUtilities.invokeLater {
                            compressionCountLabel.text = "Invalid or expired API key"
                        }
                    }
                } else {
                    SwingUtilities.invokeLater {
                        compressionCountLabel.text = "No API key configured"
                    }
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    compressionCountLabel.text = "Unable to load compression count"
                }
            }
        }.start()
    }

    override fun isModified(): Boolean {
        val currentKeys = mutableListOf<String>()
        for (i in 0 until apiKeyListModel.size()) {
            currentKeys.add(apiKeyListModel.getElementAt(i))
        }
        
        return currentKeys != settings.apiKeys ||
                autoSwitchCheckbox.isSelected != settings.autoSwitchKey ||
                showNotificationsCheckbox.isSelected != settings.showNotifications ||
                preserveOriginalCheckbox.isSelected != settings.preserveOriginalFiles ||
                resizeMethodCombo.selectedItem != settings.defaultResizeMethod
    }

    override fun apply() {
        val newKeys = mutableListOf<String>()
        for (i in 0 until apiKeyListModel.size()) {
            newKeys.add(apiKeyListModel.getElementAt(i))
        }
        
        settings.apiKeys = newKeys
        settings.autoSwitchKey = autoSwitchCheckbox.isSelected
        settings.showNotifications = showNotificationsCheckbox.isSelected
        settings.preserveOriginalFiles = preserveOriginalCheckbox.isSelected
        settings.defaultResizeMethod = resizeMethodCombo.selectedItem as ResizeMethod
    }

    override fun reset() {
        apiKeyListModel.clear()
        settings.apiKeys.forEach { apiKeyListModel.addElement(it) }
        
        autoSwitchCheckbox.isSelected = settings.autoSwitchKey
        showNotificationsCheckbox.isSelected = settings.showNotifications
        preserveOriginalCheckbox.isSelected = settings.preserveOriginalFiles
        resizeMethodCombo.selectedItem = settings.defaultResizeMethod
    }

    override fun disposeUIResources() {
        panel = null
    }
    
    /**
     * Cell renderer for API keys that marks the active one.
     */
    private class ApiKeyCellRenderer(private val settings: TinyPngSettings) : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): java.awt.Component {
            val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            
            if (component is JLabel && value is String) {
                val maskedKey = maskApiKey(value)
                val isActive = index == settings.currentKeyIndex
                text = if (isActive) "$maskedKey  [Active]" else maskedKey
            }
            
            return component
        }
        
        private fun maskApiKey(key: String): String {
            return if (key.length > 8) {
                "${key.take(4)}${"*".repeat(key.length - 8)}${key.takeLast(4)}"
            } else {
                "*".repeat(key.length)
            }
        }
    }
    
    /**
     * Cell renderer for resize methods.
     */
    private class ResizeMethodRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): java.awt.Component {
            val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            
            if (component is JLabel && value is ResizeMethod) {
                text = value.displayName
                toolTipText = value.description
            }
            
            return component
        }
    }
}
