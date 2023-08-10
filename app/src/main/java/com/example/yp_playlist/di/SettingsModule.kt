package com.example.yp_playlist.di

import android.content.Context
import com.example.yp_playlist.App
import com.example.yp_playlist.settings.data.SettingsRepository
import com.example.yp_playlist.settings.data.impl.SettingsRepositoryImpl
import com.example.yp_playlist.settings.data.storage.SettingsThemeStorage
import com.example.yp_playlist.settings.data.storage.SharedPrefsThemeStorage
import com.example.yp_playlist.settings.domain.SettingsInteractor
import com.example.yp_playlist.settings.domain.impl.SettingsInteractorImpl
import com.example.yp_playlist.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val settingsModule = module {

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get(),
            androidApplication() as App
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(storage = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<SettingsThemeStorage> {
        SharedPrefsThemeStorage(sharedPreferences = get())
    }

    single {
        androidContext()
            .getSharedPreferences(
                "local_storage", Context.MODE_PRIVATE
            )
    }

}