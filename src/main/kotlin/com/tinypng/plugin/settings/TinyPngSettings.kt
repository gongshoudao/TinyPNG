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

package com.tinypng.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.tinypng.plugin.models.ResizeMethod

/**
 * Persistent settings for the TinyPNG plugin.
 */
@State(
    name = "com.tinypng.plugin.settings.TinyPngSettings",
    storages = [Storage("tinypng.xml")]
)
@Service(Service.Level.APP)
class TinyPngSettings : PersistentStateComponent<TinyPngSettings.State> {
    
    private var myState = State()

    /**
     * Settings state that will be persisted.
     */
    data class State(
        var apiKeys: MutableList<String> = mutableListOf(),
        var currentKeyIndex: Int = 0,
        var autoSwitchKey: Boolean = true,
        var showNotifications: Boolean = true,
        var defaultResizeMethod: String = ResizeMethod.FIT.name,
        var preserveOriginalFiles: Boolean = false,
        var replaceOriginal: Boolean = true,
        var createBackup: Boolean = false
    )

    override fun getState(): State = myState

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, myState)
    }

    /**
     * Get the list of API keys.
     */
    var apiKeys: MutableList<String>
        get() = myState.apiKeys
        set(value) {
            myState.apiKeys = value
        }

    /**
     * Get the current active API key index.
     */
    var currentKeyIndex: Int
        get() = myState.currentKeyIndex
        set(value) {
            myState.currentKeyIndex = value
        }

    /**
     * Whether to automatically switch to the next key when limit is reached.
     */
    var autoSwitchKey: Boolean
        get() = myState.autoSwitchKey
        set(value) {
            myState.autoSwitchKey = value
        }

    /**
     * Whether to show notifications after compression.
     */
    var showNotifications: Boolean
        get() = myState.showNotifications
        set(value) {
            myState.showNotifications = value
        }

    /**
     * The default resize method.
     */
    var defaultResizeMethod: ResizeMethod
        get() = try {
            ResizeMethod.valueOf(myState.defaultResizeMethod)
        } catch (e: Exception) {
            ResizeMethod.FIT
        }
        set(value) {
            myState.defaultResizeMethod = value.name
        }

    /**
     * Whether to preserve original files.
     */
    var preserveOriginalFiles: Boolean
        get() = myState.preserveOriginalFiles
        set(value) {
            myState.preserveOriginalFiles = value
        }

    /**
     * Get the current active API key, or null if none configured.
     */
    fun getCurrentApiKey(): String? {
        if (apiKeys.isEmpty()) return null
        if (currentKeyIndex >= apiKeys.size) {
            currentKeyIndex = 0
        }
        return apiKeys.getOrNull(currentKeyIndex)
    }

    /**
     * Switch to the next API key.
     * @return true if switched successfully, false if no more keys available
     */
    fun switchToNextKey(): Boolean {
        if (apiKeys.size <= 1) return false
        currentKeyIndex = (currentKeyIndex + 1) % apiKeys.size
        return true
    }

    /**
     * Add a new API key.
     */
    fun addApiKey(key: String) {
        if (key.isNotBlank() && key !in apiKeys) {
            apiKeys.add(key.trim())
        }
    }

    /**
     * Remove an API key.
     */
    fun removeApiKey(key: String) {
        val index = apiKeys.indexOf(key)
        if (index >= 0) {
            apiKeys.removeAt(index)
            if (currentKeyIndex >= apiKeys.size) {
                currentKeyIndex = maxOf(0, apiKeys.size - 1)
            }
        }
    }

    /**
     * Check if any API keys are configured.
     */
    fun hasApiKeys(): Boolean = apiKeys.isNotEmpty()

    companion object {
        fun getInstance(): TinyPngSettings {
            return ApplicationManager.getApplication().getService(TinyPngSettings::class.java)
        }
    }
}
