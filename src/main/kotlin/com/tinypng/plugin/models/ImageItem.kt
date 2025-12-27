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

package com.tinypng.plugin.models

import com.intellij.openapi.vfs.VirtualFile

/**
 * Represents an image item in the compression dialog.
 */
data class ImageItem(
    val file: VirtualFile,
    val originalSize: Long,
    var compressedSize: Long? = null,
    var compressedData: ByteArray? = null,
    var isSelected: Boolean = true,
    var status: CompressionStatus = CompressionStatus.PENDING,
    var errorMessage: String? = null,
    var outputExtension: String? = null
) {
    /**
     * Calculate the percentage of space saved after compression.
     */
    val savingsPercent: Int
        get() = compressedSize?.let {
            if (originalSize > 0) ((originalSize - it) * 100 / originalSize).toInt() else 0
        } ?: 0

    /**
     * Calculate the bytes saved after compression.
     */
    val bytesSaved: Long
        get() = compressedSize?.let { originalSize - it } ?: 0

    /**
     * Get the display name of the file.
     */
    val displayName: String
        get() = file.name

    /**
     * Get the relative path within the project.
     */
    val relativePath: String
        get() = file.path

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageItem

        if (file.path != other.file.path) return false
        if (originalSize != other.originalSize) return false
        if (compressedSize != other.compressedSize) return false
        if (isSelected != other.isSelected) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.path.hashCode()
        result = 31 * result + originalSize.hashCode()
        result = 31 * result + (compressedSize?.hashCode() ?: 0)
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}

/**
 * Status of image compression.
 */
enum class CompressionStatus {
    PENDING,
    COMPRESSING,
    COMPLETED,
    FAILED,
    SKIPPED
}
