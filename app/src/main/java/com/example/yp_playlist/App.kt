package com.example.yp_playlist


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.yp_playlist.di.mediaModule
import com.example.yp_playlist.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import com.example.yp_playlist.di.settingsModule
import com.example.yp_playlist.di.sharingModule
import com.example.yp_playlist.presentation.search.PLAYLIST_SHARED_PREFERENCES
import com.example.yp_playlist.settings.data.storage.SharedPrefsThemeStorage


class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(settingsModule, sharingModule, searchModule, mediaModule))
        }

        val sharedPrefs = getSharedPreferences(PLAYLIST_SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(SharedPrefsThemeStorage.DARK_THEME, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}