package com.example.yp_playlist.settings.domain

import com.example.yp_playlist.settings.domain.models.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}