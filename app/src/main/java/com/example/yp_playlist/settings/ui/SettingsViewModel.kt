package com.example.yp_playlist.settings.ui


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yp_playlist.App
import com.example.yp_playlist.creator.Creator
import com.example.yp_playlist.data.date.SearchHistoryImpl
import com.example.yp_playlist.data.network.NetworkClientImpl
import com.example.yp_playlist.domain.interactor.TracksInteractorImpl
import com.example.yp_playlist.domain.repository.TracksRepositoryImpl
import com.example.yp_playlist.settings.data.impl.SettingsRepositoryImpl
import com.example.yp_playlist.settings.data.storage.SharedPrefsThemeStorage
import com.example.yp_playlist.settings.domain.SettingsInteractor
import com.example.yp_playlist.settings.domain.impl.SettingsInteractorImpl
import com.example.yp_playlist.settings.domain.models.ThemeSettings
import com.example.yp_playlist.sharing.SharingInteractor
import com.example.yp_playlist.sharing.data.impl.ExternalNavigatorImpl
import com.example.yp_playlist.sharing.domain.impl.SharingInteractorImpl

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

    companion object {

        fun getViewModelFactory(context: Context,sharedPref: SharedPreferences): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                    SettingsViewModel(
                                SettingsInteractorImpl(
                                    SettingsRepositoryImpl(
                                        SharedPrefsThemeStorage(sharedPref)
                                    )),
                                SharingInteractorImpl(
                                    ExternalNavigatorImpl(context)
                                ),
                                application )
                }
            }
    }

}