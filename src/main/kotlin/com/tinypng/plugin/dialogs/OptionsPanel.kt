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

package com.tinypng.plugin.dialogs

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.JBUI
import com.tinypng.plugin.models.*
import com.tinypng.plugin.settings.TinyPngSettings
import java.awt.FlowLayout
import javax.swing.*

/**
 * Panel for configuring compression options.
 */
class OptionsPanel : JPanel() {
    
    // Resize options
    private val resizeCheckbox = JBCheckBox("Resize image")
    private val resizeMethodCombo = JComboBox(ResizeMethod.entries.toTypedArray())
    private val widthField = JBTextField(6)
    private val heightField = JBTextField(6)
    
    // Convert options
    private val convertCheckbox = JBCheckBox("Convert format")
    private val formatCombo = JComboBox(arrayOf(
        "Keep original",
        "PNG",
        "JPEG",
        "WebP",
        "AVIF",
        "Smallest"
    ))
    private val backgroundLabel = JBLabel("Background:")
    private val backgroundField = JBTextField("#FFFFFF", 8)
    
    // Preserve metadata options
    private val preserveCopyrightCheckbox = JBCheckBox("Copyright")
    private val preserveLocationCheckbox = JBCheckBox("GPS Location")
    private val preserveCreationCheckbox = JBCheckBox("Creation Date")
    
    private val settings = TinyPngSettings.getInstance()
    
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.empty(5)
        
        // Initialize with default values
        resizeMethodCombo.selectedItem = settings.defaultResizeMethod
        resizeMethodCombo.renderer = ResizeMethodRenderer()
        
        // Resize panel
        add(createResizePanel())
        
        // Convert panel
        add(Box.createVerticalStrut(5))
        add(createConvertPanel())
        
        // Metadata panel
        add(Box.createVerticalStrut(5))
        add(createMetadataPanel())
        
        // Setup listeners
        setupListeners()
        
        // Initial state
        updateResizeFieldsEnabled()
        updateConvertFieldsEnabled()
    }
    
    private fun createResizePanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        panel.border = JBUI.Borders.emptyTop(5)
        
        panel.add(resizeCheckbox)
        panel.add(JBLabel("Method:"))
        panel.add(resizeMethodCombo)
        panel.add(JBLabel("Width:"))
        panel.add(widthField)
        panel.add(JBLabel("Height:"))
        panel.add(heightField)
        
        return panel
    }
    
    private fun createConvertPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        
        panel.add(convertCheckbox)
        panel.add(JBLabel("To:"))
        panel.add(formatCombo)
        panel.add(backgroundLabel)
        panel.add(backgroundField)
        
        return panel
    }
    
    private fun createMetadataPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        
        panel.add(JBLabel("Preserve:"))
        panel.add(preserveCopyrightCheckbox)
        panel.add(preserveLocationCheckbox)
        panel.add(preserveCreationCheckbox)
        
        return panel
    }
    
    private fun setupListeners() {
        resizeCheckbox.addActionListener { updateResizeFieldsEnabled() }
        convertCheckbox.addActionListener { updateConvertFieldsEnabled() }
        formatCombo.addActionListener { updateBackgroundFieldEnabled() }
    }
    
    private fun updateResizeFieldsEnabled() {
        val enabled = resizeCheckbox.isSelected
        resizeMethodCombo.isEnabled = enabled
        widthField.isEnabled = enabled
        heightField.isEnabled = enabled
    }
    
    private fun updateConvertFieldsEnabled() {
        val enabled = convertCheckbox.isSelected
        formatCombo.isEnabled = enabled
        updateBackgroundFieldEnabled()
    }
    
    private fun updateBackgroundFieldEnabled() {
        val isJpeg = formatCombo.selectedItem == "JPEG"
        val enabled = convertCheckbox.isSelected && isJpeg
        backgroundLabel.isEnabled = enabled
        backgroundField.isEnabled = enabled
    }
    
    /**
     * Get the current compression options.
     */
    fun getOptions(): CompressionOptions {
        return CompressionOptions(
            resize = getResizeOptions(),
            convert = getConvertOptions(),
            preserveMetadata = getPreserveMetadata()
        )
    }
    
    private fun getResizeOptions(): ResizeOptions? {
        if (!resizeCheckbox.isSelected) return null
        
        val width = widthField.text.trim().toIntOrNull()
        val height = heightField.text.trim().toIntOrNull()
        
        if (width == null && height == null) return null
        
        return ResizeOptions(
            method = resizeMethodCombo.selectedItem as ResizeMethod,
            width = width,
            height = height
        )
    }
    
    private fun getConvertOptions(): ConvertOptions? {
        if (!convertCheckbox.isSelected) return null
        
        val selectedFormat = formatCombo.selectedItem as String
        if (selectedFormat == "Keep original") return null
        
        val types = when (selectedFormat) {
            "PNG" -> listOf(ConvertOptions.TYPE_PNG)
            "JPEG" -> listOf(ConvertOptions.TYPE_JPEG)
            "WebP" -> listOf(ConvertOptions.TYPE_WEBP)
            "AVIF" -> listOf(ConvertOptions.TYPE_AVIF)
            "Smallest" -> listOf(ConvertOptions.TYPE_SMALLEST)
            else -> return null
        }
        
        val background = if (selectedFormat == "JPEG") {
            backgroundField.text.trim().ifEmpty { null }
        } else null
        
        return ConvertOptions(types = types, background = background)
    }
    
    private fun getPreserveMetadata(): List<MetadataType> {
        val metadata = mutableListOf<MetadataType>()
        
        if (preserveCopyrightCheckbox.isSelected) {
            metadata.add(MetadataType.COPYRIGHT)
        }
        if (preserveLocationCheckbox.isSelected) {
            metadata.add(MetadataType.LOCATION)
        }
        if (preserveCreationCheckbox.isSelected) {
            metadata.add(MetadataType.CREATION)
        }
        
        return metadata
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
