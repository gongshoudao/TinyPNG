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
 * Options for image compression.
 */
data class CompressionOptions(
    val resize: ResizeOptions? = null,
    val convert: ConvertOptions? = null,
    val preserveMetadata: List<MetadataType> = emptyList()
) {
    companion object {
        val DEFAULT = CompressionOptions()
    }
}

/**
 * Options for resizing images.
 */
data class ResizeOptions(
    val method: ResizeMethod,
    val width: Int? = null,
    val height: Int? = null
) {
    init {
        require(width != null || height != null) { "At least one dimension must be specified" }
        require(width == null || width > 0) { "Width must be positive" }
        require(height == null || height > 0) { "Height must be positive" }
    }
}

/**
 * Resize methods supported by TinyPNG.
 */
enum class ResizeMethod(val apiValue: String, val displayName: String, val description: String) {
    SCALE("scale", "Scale", "Scales the image down proportionally. Provide either a target width or height."),
    FIT("fit", "Fit", "Scales the image to fit within the given dimensions while preserving aspect ratio."),
    COVER("cover", "Cover", "Scales the image to cover the given dimensions while preserving aspect ratio."),
    THUMB("thumb", "Thumb", "Creates thumbnail using intelligent cropping to focus on important areas.")
}

/**
 * Options for format conversion.
 */
data class ConvertOptions(
    val types: List<String>,
    val background: String? = null
) {
    init {
        require(types.isNotEmpty()) { "At least one target type must be specified" }
    }

    companion object {
        const val TYPE_PNG = "image/png"
        const val TYPE_JPEG = "image/jpeg"
        const val TYPE_WEBP = "image/webp"
        const val TYPE_AVIF = "image/avif"
        const val TYPE_SMALLEST = "*/*"

        val ALL_TYPES = listOf(TYPE_PNG, TYPE_JPEG, TYPE_WEBP, TYPE_AVIF)
    }
}

/**
 * Metadata types that can be preserved during compression.
 */
enum class MetadataType(val apiValue: String, val displayName: String) {
    COPYRIGHT("copyright", "Copyright"),
    CREATION("creation", "Creation Date"),
    LOCATION("location", "GPS Location")
}
