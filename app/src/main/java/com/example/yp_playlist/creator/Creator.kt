package com.example.yp_playlist.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.data.date.DateManagerImpl
import com.example.yp_playlist.data.network.NetworkClientImpl
import com.example.yp_playlist.domain.storage.SearchHistory
import com.example.yp_playlist.domain.storage.SearchHistoryImpl
import com.example.yp_playlist.domain.interactor.TracksInteractorImpl
import com.example.yp_playlist.domain.repository.TracksRepositoryImpl
import com.example.yp_playlist.presentation.media.MediaPresenter
import com.example.yp_playlist.presentation.media.MediaView
import com.example.yp_playlist.presentation.search.HISTORY_TRACKS_SHARED_PREF
import com.example.yp_playlist.presentation.search.SearchPresenter
import com.example.yp_playlist.presentation.search.SearchView
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

    fun provideSearchPresenter(
        view: SearchView,
        sharedPref: SharedPreferences
    ): SearchPresenter {
        return SearchPresenter(
            view = view,
            tracksInteractor = TracksInteractorImpl(
                TracksRepositoryImpl(
                    NetworkClientImpl(),
                    SearchHistoryImpl(sharedPref)
                )
            )
        )
    }

    fun provideMediaPresenter(
        view: MediaView,
        sharedPref: SharedPreferences
    ): MediaPresenter {
        return MediaPresenter(
            view = view,
            dateManager = DateManagerImpl(),
            searchHistory = SearchHistoryImpl(sharedPref)
        )
    }
}





