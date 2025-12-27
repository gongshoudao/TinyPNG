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

package com.tinypng.plugin.utils

import com.intellij.openapi.vfs.VirtualFile
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.text.DecimalFormat
import javax.imageio.ImageIO

/**
 * Utility functions for image handling.
 */
object ImageUtils {
    /**
     * Supported image extensions.
     */
    val SUPPORTED_EXTENSIONS = setOf("png", "jpg", "jpeg", "webp", "avif")

    /**
     * Check if a file is a supported image file.
     */
    fun isImageFile(file: VirtualFile): Boolean {
        if (file.isDirectory) return false
        val extension = file.extension?.lowercase() ?: return false
        return extension in SUPPORTED_EXTENSIONS
    }

    /**
     * Recursively collect all image files from the given files/directories.
     */
    fun collectImageFiles(files: Array<VirtualFile>): List<VirtualFile> {
        val result = mutableListOf<VirtualFile>()
        
        fun collect(file: VirtualFile) {
            if (file.isDirectory) {
                file.children.forEach { collect(it) }
            } else if (isImageFile(file)) {
                result.add(file)
            }
        }
        
        files.forEach { collect(it) }
        return result.sortedBy { it.path }
    }

    /**
     * Load an image from a VirtualFile.
     */
    fun loadImage(file: VirtualFile): BufferedImage? {
        return try {
            file.inputStream.use { stream ->
                ImageIO.read(stream)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Load an image from a byte array.
     */
    fun loadImage(data: ByteArray): BufferedImage? {
        return try {
            ByteArrayInputStream(data).use { stream ->
                ImageIO.read(stream)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format file size to human-readable format.
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        
        val units = arrayOf("B", "KiB", "MiB", "GiB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        val df = DecimalFormat("#.#")
        return "${df.format(size)} ${units[unitIndex]}"
    }

    /**
     * Format percentage.
     */
    fun formatPercentage(percent: Int): String {
        return "$percent%"
    }

    /**
     * Get the MIME type for an image extension.
     */
    fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            "avif" -> "image/avif"
            else -> "application/octet-stream"
        }
    }

    /**
     * Get the file extension for a MIME type.
     */
    fun getExtensionForMimeType(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/png" -> "png"
            "image/jpeg" -> "jpg"
            "image/webp" -> "webp"
            "image/avif" -> "avif"
            else -> "bin"
        }
    }

    /**
     * Get image dimensions as a string.
     */
    fun getImageDimensions(image: BufferedImage): String {
        return "${image.width}x${image.height}"
    }
}
