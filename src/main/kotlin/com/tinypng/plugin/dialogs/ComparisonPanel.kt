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

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import com.tinypng.plugin.utils.ImageUtils
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import javax.swing.*

/**
 * Panel for comparing original and compressed images.
 * Supports side-by-side and overlay (slider) comparison modes.
 */
class ComparisonPanel : JPanel(BorderLayout()) {
    
    private var originalImage: BufferedImage? = null
    private var compressedImage: BufferedImage? = null
    private var originalSize: Long = 0
    private var compressedSize: Long = 0
    
    private var comparisonMode = ComparisonMode.SIDE_BY_SIDE
    private var sliderPosition = 0.5 // For overlay mode (0.0 to 1.0)
    
    private val imagePanel = ImageComparisonCanvas()
    private val modeToggle = JToggleButton("Overlay Mode")
    private val originalSizeLabel = JBLabel("")
    private val compressedSizeLabel = JBLabel("")
    
    init {
        // Top: mode toggle and labels
        val topPanel = JPanel(BorderLayout())
        topPanel.border = JBUI.Borders.emptyBottom(5)
        
        val labelsPanel = JPanel(GridLayout(1, 2, 10, 0))
        
        val beforeLabel = JBLabel("Before", SwingConstants.CENTER)
        beforeLabel.font = beforeLabel.font.deriveFont(Font.BOLD)
        labelsPanel.add(beforeLabel)
        
        val afterLabel = JBLabel("After", SwingConstants.CENTER)
        afterLabel.font = afterLabel.font.deriveFont(Font.BOLD)
        labelsPanel.add(afterLabel)
        
        topPanel.add(labelsPanel, BorderLayout.CENTER)
        
        modeToggle.addActionListener {
            comparisonMode = if (modeToggle.isSelected) {
                ComparisonMode.OVERLAY
            } else {
                ComparisonMode.SIDE_BY_SIDE
            }
            imagePanel.repaint()
        }
        topPanel.add(modeToggle, BorderLayout.EAST)
        
        add(topPanel, BorderLayout.NORTH)
        
        // Center: image comparison
        add(imagePanel, BorderLayout.CENTER)
        
        // Bottom: size labels
        val bottomPanel = JPanel(GridLayout(1, 2, 10, 0))
        bottomPanel.border = JBUI.Borders.emptyTop(5)
        
        originalSizeLabel.horizontalAlignment = SwingConstants.CENTER
        compressedSizeLabel.horizontalAlignment = SwingConstants.CENTER
        
        bottomPanel.add(originalSizeLabel)
        bottomPanel.add(compressedSizeLabel)
        
        add(bottomPanel, BorderLayout.SOUTH)
    }
    
    /**
     * Set the original image to display.
     */
    fun setOriginalImage(image: BufferedImage, size: Long) {
        originalImage = image
        originalSize = size
        originalSizeLabel.text = "Size: ${ImageUtils.formatFileSize(size)}"
        compressedImage = null
        compressedSize = 0
        compressedSizeLabel.text = ""
        imagePanel.repaint()
    }
    
    /**
     * Set the compressed image to display.
     */
    fun setCompressedImage(image: BufferedImage, size: Long) {
        compressedImage = image
        compressedSize = size
        compressedSizeLabel.text = "Size: ${ImageUtils.formatFileSize(size)}"
        imagePanel.repaint()
    }
    
    /**
     * Set the comparison mode.
     */
    fun setComparisonMode(mode: ComparisonMode) {
        comparisonMode = mode
        modeToggle.isSelected = mode == ComparisonMode.OVERLAY
        imagePanel.repaint()
    }
    
    /**
     * Comparison modes.
     */
    enum class ComparisonMode {
        SIDE_BY_SIDE,
        OVERLAY
    }
    
    /**
     * Canvas for rendering the image comparison.
     */
    private inner class ImageComparisonCanvas : JPanel() {
        
        init {
            background = JBColor.background()
            preferredSize = Dimension(600, 400)
            
            // Mouse handling for overlay mode slider
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    if (comparisonMode == ComparisonMode.OVERLAY) {
                        updateSliderPosition(e.x)
                    }
                }
            })
            
            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(e: MouseEvent) {
                    if (comparisonMode == ComparisonMode.OVERLAY) {
                        updateSliderPosition(e.x)
                    }
                }
            })
        }
        
        private fun updateSliderPosition(x: Int) {
            sliderPosition = (x.toDouble() / width).coerceIn(0.0, 1.0)
            repaint()
        }
        
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            
            val original = originalImage ?: return
            
            when (comparisonMode) {
                ComparisonMode.SIDE_BY_SIDE -> drawSideBySide(g2d, original)
                ComparisonMode.OVERLAY -> drawOverlay(g2d, original)
            }
        }
        
        private fun drawSideBySide(g2d: Graphics2D, original: BufferedImage) {
            val panelWidth = width
            val panelHeight = height
            val halfWidth = panelWidth / 2 - 5
            
            // Calculate scaled dimensions to fit each half
            val scale = calculateScale(original, halfWidth, panelHeight)
            val scaledWidth = (original.width * scale).toInt()
            val scaledHeight = (original.height * scale).toInt()
            
            // Draw original on the left
            val leftX = (halfWidth - scaledWidth) / 2
            val y = (panelHeight - scaledHeight) / 2
            g2d.drawImage(original, leftX, y, scaledWidth, scaledHeight, null)
            
            // Draw border around original
            g2d.color = JBColor.border()
            g2d.drawRect(leftX - 1, y - 1, scaledWidth + 1, scaledHeight + 1)
            
            // Draw compressed on the right (if available)
            val compressed = compressedImage
            if (compressed != null) {
                val rightX = halfWidth + 10 + (halfWidth - scaledWidth) / 2
                g2d.drawImage(compressed, rightX, y, scaledWidth, scaledHeight, null)
                
                // Draw border
                g2d.drawRect(rightX - 1, y - 1, scaledWidth + 1, scaledHeight + 1)
            } else {
                // Draw placeholder
                val rightX = halfWidth + 10
                g2d.color = JBColor.border()
                g2d.drawRect(rightX, y, halfWidth - 20, scaledHeight)
                
                g2d.color = JBColor.GRAY
                val message = "Compress to preview"
                val fm = g2d.fontMetrics
                val textX = rightX + (halfWidth - 20 - fm.stringWidth(message)) / 2
                val textY = y + scaledHeight / 2 + fm.ascent / 2
                g2d.drawString(message, textX, textY)
            }
            
            // Draw divider line
            g2d.color = JBColor.border()
            g2d.drawLine(halfWidth + 5, 0, halfWidth + 5, panelHeight)
        }
        
        private fun drawOverlay(g2d: Graphics2D, original: BufferedImage) {
            val compressed = compressedImage
            if (compressed == null) {
                // Just draw the original if no compressed version
                drawSideBySide(g2d, original)
                return
            }
            
            val panelWidth = width
            val panelHeight = height
            
            // Calculate scaled dimensions
            val scale = calculateScale(original, panelWidth, panelHeight)
            val scaledWidth = (original.width * scale).toInt()
            val scaledHeight = (original.height * scale).toInt()
            
            val x = (panelWidth - scaledWidth) / 2
            val y = (panelHeight - scaledHeight) / 2
            
            // Draw compressed (background)
            g2d.drawImage(compressed, x, y, scaledWidth, scaledHeight, null)
            
            // Draw original with clipping (left side of slider)
            val sliderX = x + (scaledWidth * sliderPosition).toInt()
            val clip = g2d.clip
            g2d.setClip(x, y, (scaledWidth * sliderPosition).toInt(), scaledHeight)
            g2d.drawImage(original, x, y, scaledWidth, scaledHeight, null)
            g2d.clip = clip
            
            // Draw slider line
            g2d.color = JBColor.WHITE
            g2d.stroke = BasicStroke(2f)
            g2d.drawLine(sliderX, y, sliderX, y + scaledHeight)
            
            // Draw slider handle
            val handleSize = 20
            g2d.fillOval(sliderX - handleSize/2, y + scaledHeight/2 - handleSize/2, handleSize, handleSize)
            g2d.color = JBColor.DARK_GRAY
            g2d.drawOval(sliderX - handleSize/2, y + scaledHeight/2 - handleSize/2, handleSize, handleSize)
            
            // Draw labels
            g2d.color = JBColor.WHITE
            g2d.font = g2d.font.deriveFont(Font.BOLD, 12f)
            
            // Before label
            if (sliderPosition > 0.15) {
                g2d.drawString("Before", x + 10, y + 25)
            }
            
            // After label
            if (sliderPosition < 0.85) {
                val afterText = "After"
                val fm = g2d.fontMetrics
                g2d.drawString(afterText, x + scaledWidth - fm.stringWidth(afterText) - 10, y + 25)
            }
            
            // Draw border
            g2d.color = JBColor.border()
            g2d.stroke = BasicStroke(1f)
            g2d.drawRect(x - 1, y - 1, scaledWidth + 1, scaledHeight + 1)
        }
        
        private fun calculateScale(image: BufferedImage, maxWidth: Int, maxHeight: Int): Double {
            val scaleX = maxWidth.toDouble() / image.width
            val scaleY = maxHeight.toDouble() / image.height
            return minOf(scaleX, scaleY, 1.0) // Don't scale up
        }
    }
}
