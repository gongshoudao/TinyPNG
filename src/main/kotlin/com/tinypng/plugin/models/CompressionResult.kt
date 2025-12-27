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

/**
 * Result of a compression operation.
 */
data class CompressionResult(
    val success: Boolean,
    val originalSize: Long,
    val compressedSize: Long,
    val compressedData: ByteArray?,
    val outputType: String? = null,
    val outputExtension: String? = null,
    val errorMessage: String? = null
) {
    /**
     * Calculate the percentage of space saved.
     */
    val savingsPercent: Int
        get() = if (originalSize > 0) ((originalSize - compressedSize) * 100 / originalSize).toInt() else 0

    /**
     * Calculate the bytes saved.
     */
    val bytesSaved: Long
        get() = originalSize - compressedSize

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompressionResult

        if (success != other.success) return false
        if (originalSize != other.originalSize) return false
        if (compressedSize != other.compressedSize) return false
        if (outputType != other.outputType) return false
        if (errorMessage != other.errorMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + originalSize.hashCode()
        result = 31 * result + compressedSize.hashCode()
        result = 31 * result + (outputType?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun failure(originalSize: Long, errorMessage: String): CompressionResult {
            return CompressionResult(
                success = false,
                originalSize = originalSize,
                compressedSize = originalSize,
                compressedData = null,
                errorMessage = errorMessage
            )
        }
    }
}
