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

package com.tinypng.plugin.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.tinypng.plugin.dialogs.CompressionDialog
import com.tinypng.plugin.settings.TinyPngSettings
import com.tinypng.plugin.utils.ImageUtils
import com.tinypng.plugin.utils.NotificationUtils

/**
 * Action for compressing images using TinyPNG.
 * This action appears in the right-click context menu of the Project View.
 */
class TinyPngAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        
        // Check if API key is configured
        val settings = TinyPngSettings.getInstance()
        if (!settings.hasApiKeys()) {
            NotificationUtils.showWarning(
                project,
                "TinyPNG",
                "No API key configured. Please configure your API key in Settings → Tools → TinyPNG"
            )
            return
        }
        
        // Collect all image files
        val imageFiles = ImageUtils.collectImageFiles(files)
        
        if (imageFiles.isEmpty()) {
            NotificationUtils.showWarning(
                project,
                "TinyPNG",
                "No supported image files found. Supported formats: PNG, JPEG, WebP, AVIF"
            )
            return
        }
        
        // Show the compression dialog
        val dialog = CompressionDialog(project, imageFiles)
        dialog.show()
    }

    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val hasImageFiles = files?.any { hasImageFilesRecursive(it) } ?: false
        
        e.presentation.isEnabledAndVisible = hasImageFiles
    }
    
    /**
     * Check if a file or directory contains image files.
     */
    private fun hasImageFilesRecursive(file: VirtualFile): Boolean {
        if (file.isDirectory) {
            return file.children.any { hasImageFilesRecursive(it) }
        }
        return ImageUtils.isImageFile(file)
    }
}
