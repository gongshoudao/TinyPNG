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

package com.tinypng.plugin.services

import com.tinify.*
import com.tinypng.plugin.models.*
import com.tinypng.plugin.settings.TinyPngSettings
import java.io.File
import java.nio.file.Files

/**
 * Service for interacting with the TinyPNG API.
 */
class TinyPngService {
    
    private val settings = TinyPngSettings.getInstance()
    
    /**
     * Initialize the service with the current API key.
     */
    private fun initializeWithCurrentKey(): Boolean {
        val key = settings.getCurrentApiKey() ?: return false
        Tinify.setKey(key)
        return true
    }
    
    /**
     * Validate an API key.
     */
    fun validateKey(apiKey: String): Boolean {
        return try {
            Tinify.setKey(apiKey)
            Tinify.validate()
            true
        } catch (e: AccountException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get the compression count for the current month.
     */
    fun getCompressionCount(): Int {
        return try {
            Tinify.compressionCount()
        } catch (e: Exception) {
            -1
        }
    }
    
    /**
     * Compress an image file.
     */
    fun compress(file: File, options: CompressionOptions = CompressionOptions.DEFAULT): CompressionResult {
        if (!initializeWithCurrentKey()) {
            return CompressionResult.failure(file.length(), "No API key configured")
        }
        
        return try {
            compressWithRetry(file, options)
        } catch (e: Exception) {
            CompressionResult.failure(file.length(), e.message ?: "Unknown error")
        }
    }
    
    /**
     * Compress with retry on account limit error.
     */
    private fun compressWithRetry(file: File, options: CompressionOptions): CompressionResult {
        return try {
            performCompression(file, options)
        } catch (e: AccountException) {
            // Try switching to next key if auto-switch is enabled
            if (settings.autoSwitchKey && settings.switchToNextKey()) {
                initializeWithCurrentKey()
                performCompression(file, options)
            } else {
                throw e
            }
        }
    }
    
    /**
     * Perform the actual compression.
     */
    private fun performCompression(file: File, options: CompressionOptions): CompressionResult {
        val originalSize = file.length()
        
        // Start with the source
        var source = Tinify.fromFile(file.absolutePath)
        
        // Apply resize if specified
        options.resize?.let { resize ->
            source = applyResize(source, resize)
        }
        
        // Apply format conversion if specified
        var outputExtension: String? = null
        var outputType: String? = null
        
        options.convert?.let { convert ->
            val result = applyConvert(source, convert)
            source = result.first
            outputType = result.second
            outputExtension = result.third
        }
        
        // Apply metadata preservation if specified
        if (options.preserveMetadata.isNotEmpty()) {
            source = applyPreserveMetadata(source, options.preserveMetadata)
        }
        
        // Get the compressed data
        val compressedData = source.toBuffer()
        
        return CompressionResult(
            success = true,
            originalSize = originalSize,
            compressedSize = compressedData.size.toLong(),
            compressedData = compressedData,
            outputType = outputType,
            outputExtension = outputExtension
        )
    }
    
    /**
     * Apply resize options to the source.
     */
    private fun applyResize(source: Source, resize: ResizeOptions): Source {
        val options = Options()
            .with("method", resize.method.apiValue)
        
        resize.width?.let { options.with("width", it) }
        resize.height?.let { options.with("height", it) }
        
        return source.resize(options)
    }
    
    /**
     * Apply format conversion to the source.
     * Returns triple of (source, outputType, outputExtension)
     */
    private fun applyConvert(source: Source, convert: ConvertOptions): Triple<Source, String?, String?> {
        val typeValue: Any = if (convert.types.size == 1) {
            convert.types[0]
        } else {
            convert.types.toTypedArray()
        }
        
        val convertOptions = Options().with("type", typeValue)
        var convertedSource = source.convert(convertOptions)
        
        // Apply background color for transparent images if specified
        convert.background?.let { bg ->
            convertedSource = convertedSource.transform(Options().with("background", bg))
        }
        
        // Get the result to determine the output type
        val result = convertedSource.result()
        val outputType = result.mediaType()
        val outputExtension = when (outputType) {
            "image/png" -> "png"
            "image/jpeg" -> "jpg"
            "image/webp" -> "webp"
            "image/avif" -> "avif"
            else -> null
        }
        
        return Triple(convertedSource, outputType, outputExtension)
    }
    
    /**
     * Apply metadata preservation to the source.
     */
    private fun applyPreserveMetadata(source: Source, metadata: List<MetadataType>): Source {
        val metadataValues = metadata.map { it.apiValue }.toTypedArray()
        return source.preserve(*metadataValues)
    }
    
    /**
     * Compress image data from a byte array.
     */
    fun compressFromBuffer(data: ByteArray, options: CompressionOptions = CompressionOptions.DEFAULT): CompressionResult {
        if (!initializeWithCurrentKey()) {
            return CompressionResult.failure(data.size.toLong(), "No API key configured")
        }
        
        return try {
            compressBufferWithRetry(data, options)
        } catch (e: Exception) {
            CompressionResult.failure(data.size.toLong(), e.message ?: "Unknown error")
        }
    }
    
    /**
     * Compress buffer with retry on account limit error.
     */
    private fun compressBufferWithRetry(data: ByteArray, options: CompressionOptions): CompressionResult {
        return try {
            performBufferCompression(data, options)
        } catch (e: AccountException) {
            if (settings.autoSwitchKey && settings.switchToNextKey()) {
                initializeWithCurrentKey()
                performBufferCompression(data, options)
            } else {
                throw e
            }
        }
    }
    
    /**
     * Perform compression on buffer data.
     */
    private fun performBufferCompression(data: ByteArray, options: CompressionOptions): CompressionResult {
        val originalSize = data.size.toLong()
        
        var source = Tinify.fromBuffer(data)
        
        options.resize?.let { resize ->
            source = applyResize(source, resize)
        }
        
        var outputExtension: String? = null
        var outputType: String? = null
        
        options.convert?.let { convert ->
            val result = applyConvert(source, convert)
            source = result.first
            outputType = result.second
            outputExtension = result.third
        }
        
        if (options.preserveMetadata.isNotEmpty()) {
            source = applyPreserveMetadata(source, options.preserveMetadata)
        }
        
        val compressedData = source.toBuffer()
        
        return CompressionResult(
            success = true,
            originalSize = originalSize,
            compressedSize = compressedData.size.toLong(),
            compressedData = compressedData,
            outputType = outputType,
            outputExtension = outputExtension
        )
    }
}
