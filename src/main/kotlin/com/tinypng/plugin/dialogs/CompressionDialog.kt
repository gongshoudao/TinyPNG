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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.CheckboxTree
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.tinypng.plugin.models.*
import com.tinypng.plugin.services.TinyPngService
import com.tinypng.plugin.settings.TinyPngSettings
import com.tinypng.plugin.utils.ImageUtils
import com.tinypng.plugin.utils.NotificationUtils
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.io.File
import javax.swing.*
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

/**
 * Main dialog for image compression using TinyPNG.
 */
class CompressionDialog(
    private val project: Project,
    private val imageFiles: List<VirtualFile>
) : DialogWrapper(project, true) {

    private val imageItems = mutableListOf<ImageItem>()
    private lateinit var fileTree: CheckboxTree
    private lateinit var treeModel: DefaultTreeModel
    private lateinit var rootNode: CheckedTreeNode
    private lateinit var comparisonPanel: ComparisonPanel
    private lateinit var optionsPanel: OptionsPanel
    private lateinit var totalStatsLabel: JBLabel
    private lateinit var optimizeButton: JButton
    private lateinit var saveButton: JButton
    
    private val tinyPngService = TinyPngService()
    private var isCompressing = false

    init {
        title = "TinyPNG Image Optimizer"
        setOKButtonText("Close")
        setCancelButtonText("Cancel")
        
        // Initialize image items
        imageFiles.forEach { file ->
            imageItems.add(ImageItem(
                file = file,
                originalSize = file.length
            ))
        }
        
        init()
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout())
        mainPanel.preferredSize = Dimension(1000, 700)
        
        // Create the main splitter (left: tree, right: preview + options)
        val mainSplitter = JBSplitter(false, 0.3f)
        
        // Left panel - file tree
        val leftPanel = createFileTreePanel()
        mainSplitter.firstComponent = leftPanel
        
        // Right panel - preview and options
        val rightPanel = createRightPanel()
        mainSplitter.secondComponent = rightPanel
        
        mainPanel.add(mainSplitter, BorderLayout.CENTER)
        
        // Bottom panel - stats and buttons
        val bottomPanel = createBottomPanel()
        mainPanel.add(bottomPanel, BorderLayout.SOUTH)
        
        return mainPanel
    }
    
    private fun createFileTreePanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.border = JBUI.Borders.empty(5)
        
        // Build tree structure
        rootNode = CheckedTreeNode("Images")
        buildTreeStructure()
        
        treeModel = DefaultTreeModel(rootNode)
        
        fileTree = CheckboxTree(ImageTreeCellRenderer(), rootNode)
        fileTree.model = treeModel
        
        // Handle selection changes
        fileTree.addTreeSelectionListener { e ->
            val node = e.path?.lastPathComponent as? CheckedTreeNode
            val imageItem = node?.userObject as? ImageItem
            if (imageItem != null) {
                updatePreview(imageItem)
            }
        }
        
        // Expand all nodes
        expandAllNodes()
        
        val scrollPane = JBScrollPane(fileTree)
        scrollPane.preferredSize = Dimension(280, 500)
        panel.add(scrollPane, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun buildTreeStructure() {
        // Group files by directory
        val filesByDir = imageItems.groupBy { it.file.parent?.path ?: "" }
        
        filesByDir.forEach { (dirPath, items) ->
            val dirNode = CheckedTreeNode(File(dirPath).name.ifEmpty { "Root" })
            dirNode.isChecked = true
            
            items.forEach { item ->
                val fileNode = CheckedTreeNode(item)
                fileNode.isChecked = item.isSelected
                dirNode.add(fileNode)
            }
            
            rootNode.add(dirNode)
        }
    }
    
    private fun expandAllNodes() {
        for (i in 0 until fileTree.rowCount) {
            fileTree.expandRow(i)
        }
    }
    
    private fun createRightPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        
        // Top: options panel
        optionsPanel = OptionsPanel()
        optionsPanel.border = JBUI.Borders.empty(5)
        panel.add(optionsPanel, BorderLayout.NORTH)
        
        // Center: comparison panel
        comparisonPanel = ComparisonPanel()
        comparisonPanel.border = JBUI.Borders.empty(5)
        panel.add(comparisonPanel, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun createBottomPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.border = JBUI.Borders.empty(10, 5, 5, 5)
        
        // Stats label
        totalStatsLabel = JBLabel("Total: 0 images selected")
        panel.add(totalStatsLabel, BorderLayout.WEST)
        
        // Buttons panel
        val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        
        optimizeButton = JButton("Optimize")
        optimizeButton.addActionListener { startCompression() }
        buttonsPanel.add(optimizeButton)
        
        saveButton = JButton("Save")
        saveButton.isEnabled = false
        saveButton.addActionListener { saveCompressedImages() }
        buttonsPanel.add(saveButton)
        
        panel.add(buttonsPanel, BorderLayout.EAST)
        
        updateStats()
        
        return panel
    }
    
    private fun updatePreview(imageItem: ImageItem) {
        val originalImage = ImageUtils.loadImage(imageItem.file)
        if (originalImage != null) {
            comparisonPanel.setOriginalImage(originalImage, imageItem.originalSize)
            
            // Update dialog title with image info
            val dimensions = ImageUtils.getImageDimensions(originalImage)
            title = "TinyPNG Image Optimizer - ${imageItem.displayName} [$dimensions]"
        }
        
        // Show compressed image if available
        imageItem.compressedData?.let { data ->
            val compressedImage = ImageUtils.loadImage(data)
            if (compressedImage != null) {
                comparisonPanel.setCompressedImage(compressedImage, imageItem.compressedSize ?: 0)
            }
        }
    }
    
    private fun updateStats() {
        val selectedItems = getSelectedItems()
        val totalOriginalSize = selectedItems.sumOf { it.originalSize }
        val completedItems = selectedItems.filter { it.status == CompressionStatus.COMPLETED }
        
        if (completedItems.isEmpty()) {
            totalStatsLabel.text = "Total: ${selectedItems.size} images selected (${ImageUtils.formatFileSize(totalOriginalSize)})"
        } else {
            val totalCompressedSize = completedItems.sumOf { it.compressedSize ?: it.originalSize }
            val totalSaved = totalOriginalSize - totalCompressedSize
            val savingsPercent = if (totalOriginalSize > 0) {
                (totalSaved * 100 / totalOriginalSize).toInt()
            } else 0
            
            totalStatsLabel.text = "Total compress: $savingsPercent% / Saved: ${ImageUtils.formatFileSize(totalSaved)}"
        }
    }
    
    private fun getSelectedItems(): List<ImageItem> {
        val selectedItems = mutableListOf<ImageItem>()
        
        fun collectSelected(node: CheckedTreeNode) {
            val userObject = node.userObject
            if (userObject is ImageItem && node.isChecked) {
                selectedItems.add(userObject)
            }
            for (i in 0 until node.childCount) {
                val child = node.getChildAt(i) as? CheckedTreeNode
                if (child != null) {
                    collectSelected(child)
                }
            }
        }
        
        collectSelected(rootNode)
        return selectedItems
    }
    
    private fun startCompression() {
        if (isCompressing) return
        
        val selectedItems = getSelectedItems()
        if (selectedItems.isEmpty()) {
            Messages.showWarningDialog(project, "No images selected for compression.", "TinyPNG")
            return
        }
        
        // Get compression options
        val options = optionsPanel.getOptions()
        
        isCompressing = true
        optimizeButton.isEnabled = false
        
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Compressing images...", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = false
                
                val total = selectedItems.size
                var completed = 0
                
                selectedItems.forEach { item ->
                    if (indicator.isCanceled) return@forEach
                    
                    indicator.text = "Compressing ${item.displayName}..."
                    indicator.fraction = completed.toDouble() / total
                    
                    // Update status to compressing
                    item.status = CompressionStatus.COMPRESSING
                    refreshTreeNode(item)
                    
                    try {
                        val file = File(item.file.path)
                        val result = tinyPngService.compress(file, options)
                        
                        if (result.success) {
                            item.compressedSize = result.compressedSize
                            item.compressedData = result.compressedData
                            item.outputExtension = result.outputExtension
                            item.status = CompressionStatus.COMPLETED
                        } else {
                            item.status = CompressionStatus.FAILED
                            item.errorMessage = result.errorMessage
                        }
                    } catch (e: Exception) {
                        item.status = CompressionStatus.FAILED
                        item.errorMessage = e.message
                    }
                    
                    refreshTreeNode(item)
                    completed++
                    
                    // Update UI
                    ApplicationManager.getApplication().invokeLater({
                        updateStats()
                        
                        // Update preview if this item is selected
                        val selectedNode = fileTree.selectionPath?.lastPathComponent as? CheckedTreeNode
                        if ((selectedNode?.userObject as? ImageItem) == item) {
                            updatePreview(item)
                        }
                    }, ModalityState.any())
                }
            }
            
            override fun onSuccess() {
                isCompressing = false
                optimizeButton.isEnabled = true
                saveButton.isEnabled = getSelectedItems().any { it.status == CompressionStatus.COMPLETED }
                updateStats()
                
                val settings = TinyPngSettings.getInstance()
                if (settings.showNotifications) {
                    val completedCount = selectedItems.count { it.status == CompressionStatus.COMPLETED }
                    val failedCount = selectedItems.count { it.status == CompressionStatus.FAILED }
                    
                    if (failedCount > 0) {
                        NotificationUtils.showWarning(
                            project,
                            "TinyPNG",
                            "Compression completed: $completedCount succeeded, $failedCount failed"
                        )
                    } else {
                        NotificationUtils.showSuccess(
                            project,
                            "TinyPNG",
                            "Successfully compressed $completedCount images"
                        )
                    }
                }
            }
            
            override fun onCancel() {
                isCompressing = false
                optimizeButton.isEnabled = true
            }
        })
    }
    
    private fun refreshTreeNode(item: ImageItem) {
        ApplicationManager.getApplication().invokeLater({
            fun findNode(node: CheckedTreeNode): CheckedTreeNode? {
                if (node.userObject == item) return node
                for (i in 0 until node.childCount) {
                    val child = node.getChildAt(i) as? CheckedTreeNode
                    if (child != null) {
                        val found = findNode(child)
                        if (found != null) return found
                    }
                }
                return null
            }
            
            val node = findNode(rootNode)
            if (node != null) {
                treeModel.nodeChanged(node)
            }
        }, ModalityState.any())
    }
    
    private fun saveCompressedImages() {
        val itemsToSave = getSelectedItems().filter { 
            it.status == CompressionStatus.COMPLETED && it.compressedData != null 
        }
        
        if (itemsToSave.isEmpty()) {
            Messages.showWarningDialog(project, "No compressed images to save.", "TinyPNG")
            return
        }
        
        val settings = TinyPngSettings.getInstance()
        
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Saving images...", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = false
                
                var saved = 0
                var failed = 0
                
                itemsToSave.forEachIndexed { index, item ->
                    indicator.text = "Saving ${item.displayName}..."
                    indicator.fraction = index.toDouble() / itemsToSave.size
                    
                    try {
                        val originalFile = File(item.file.path)
                        
                        // Create backup if configured
                        if (settings.preserveOriginalFiles) {
                            val backupFile = File(originalFile.parent, "${originalFile.name}.backup")
                            originalFile.copyTo(backupFile, overwrite = true)
                        }
                        
                        // Determine output file name
                        val outputFile = if (item.outputExtension != null && 
                            item.outputExtension != originalFile.extension) {
                            // Format conversion - create new file
                            File(originalFile.parent, 
                                "${originalFile.nameWithoutExtension}.${item.outputExtension}")
                        } else {
                            originalFile
                        }
                        
                        // Write compressed data
                        outputFile.writeBytes(item.compressedData!!)
                        
                        // Refresh the virtual file
                        ApplicationManager.getApplication().invokeLater {
                            item.file.refresh(false, false)
                        }
                        
                        saved++
                    } catch (e: Exception) {
                        failed++
                    }
                }
                
                // Notify completion
                ApplicationManager.getApplication().invokeLater {
                    if (failed > 0) {
                        NotificationUtils.showWarning(
                            project,
                            "TinyPNG",
                            "Saved $saved images, $failed failed"
                        )
                    } else {
                        NotificationUtils.showSuccess(
                            project,
                            "TinyPNG",
                            "Successfully saved $saved images"
                        )
                    }
                }
            }
        })
    }

    override fun createActions(): Array<Action> {
        return arrayOf(okAction)
    }
    
    /**
     * Custom cell renderer for the image tree.
     */
    private inner class ImageTreeCellRenderer : CheckboxTree.CheckboxTreeCellRenderer() {
        override fun customizeRenderer(
            tree: JTree?,
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ) {
            val node = value as? CheckedTreeNode ?: return
            val userObject = node.userObject
            
            when (userObject) {
                is ImageItem -> {
                    val statusSuffix = when (userObject.status) {
                        CompressionStatus.PENDING -> ""
                        CompressionStatus.COMPRESSING -> " ⏳"
                        CompressionStatus.COMPLETED -> " ${userObject.savingsPercent}%"
                        CompressionStatus.FAILED -> " ❌"
                        CompressionStatus.SKIPPED -> " ⏭"
                    }
                    
                    textRenderer.append(userObject.displayName)
                    textRenderer.append(statusSuffix, com.intellij.ui.SimpleTextAttributes.GRAYED_ATTRIBUTES)
                }
                is String -> {
                    textRenderer.append(userObject)
                }
            }
        }
    }
}
