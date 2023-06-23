package com.example.yp_playlist.settings.data.impl

import com.example.yp_playlist.settings.data.SettingsRepository
import com.example.yp_playlist.settings.data.storage.SettingsThemeStorage
import com.example.yp_playlist.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(private val storage: SettingsThemeStorage): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return storage.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.saveThemeSettings(settings)
    }

}