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

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Utility functions for displaying notifications.
 */
object NotificationUtils {
    private const val NOTIFICATION_GROUP_ID = "TinyPNG"

    /**
     * Show a success notification.
     */
    fun showSuccess(project: Project?, title: String, message: String) {
        showNotification(project, title, message, NotificationType.INFORMATION)
    }

    /**
     * Show an error notification.
     */
    fun showError(project: Project?, title: String, message: String) {
        showNotification(project, title, message, NotificationType.ERROR)
    }

    /**
     * Show a warning notification.
     */
    fun showWarning(project: Project?, title: String, message: String) {
        showNotification(project, title, message, NotificationType.WARNING)
    }

    /**
     * Show a notification.
     */
    private fun showNotification(
        project: Project?,
        title: String,
        message: String,
        type: NotificationType
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(title, message, type)
            .notify(project)
    }
}
