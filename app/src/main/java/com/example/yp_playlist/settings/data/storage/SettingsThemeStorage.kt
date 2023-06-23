package com.example.yp_playlist.settings.data.storage

import com.example.yp_playlist.settings.domain.models.ThemeSettings

interface SettingsThemeStorage {
    fun saveThemeSettings(settings: ThemeSettings)
    fun getThemeSettings(): ThemeSettings
}