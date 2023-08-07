package com.example.yp_playlist.creator

import android.content.Context
import com.example.yp_playlist.settings.data.impl.SettingsRepositoryImpl
import com.example.yp_playlist.settings.data.storage.SharedPrefsThemeStorage
import com.example.yp_playlist.settings.domain.SettingsInteractor
import com.example.yp_playlist.settings.domain.impl.SettingsInteractorImpl
import com.example.yp_playlist.sharing.SharingInteractor
import com.example.yp_playlist.sharing.data.impl.ExternalNavigatorImpl
import com.example.yp_playlist.sharing.domain.impl.SharingInteractorImpl

object Creator {

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPrefs =
            context.getSharedPreferences(SharedPrefsThemeStorage.DARK_THEME, Context.MODE_PRIVATE)
        val repository = SettingsRepositoryImpl(SharedPrefsThemeStorage(sharedPrefs))
        return SettingsInteractorImpl(repository)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val externalNavigator = ExternalNavigatorImpl(context)
        return SharingInteractorImpl(externalNavigator)
    }


}





