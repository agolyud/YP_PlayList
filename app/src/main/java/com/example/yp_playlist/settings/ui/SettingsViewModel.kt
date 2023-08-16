package com.example.yp_playlist.settings.ui

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yp_playlist.App
import com.example.yp_playlist.settings.domain.SettingsInteractor
import com.example.yp_playlist.settings.domain.models.ThemeSettings
import com.example.yp_playlist.sharing.SharingInteractor

class SettingsViewModel (
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
    private val application: App,
) : AndroidViewModel(application) {

    private val _themeSettingsState = MutableLiveData<ThemeSettings>()
    val themeSettingsState: LiveData<ThemeSettings> = _themeSettingsState

    init {
        _themeSettingsState.postValue(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val settings = ThemeSettings(darkThemeEnabled)
        _themeSettingsState.postValue(settings)
        settingsInteractor.updateThemeSetting(settings)
        application.switchTheme(darkThemeEnabled)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun supportEmail() {
        sharingInteractor.openSupport()
    }

    fun openAgreement() {
        sharingInteractor.openTerms()
    }


}