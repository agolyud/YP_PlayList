package com.example.yp_playlist.creator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.data.date.DateManagerImpl
import com.example.yp_playlist.data.network.NetworkClientImpl
import com.example.yp_playlist.data.storage.SearchHistory
import com.example.yp_playlist.data.storage.SearchHistoryImpl
import com.example.yp_playlist.domain.interactor.TracksInteractorImpl
import com.example.yp_playlist.domain.repository.TracksRepositoryImpl
import com.example.yp_playlist.presentation.media.MediaPresenter
import com.example.yp_playlist.presentation.media.MediaView
import com.example.yp_playlist.presentation.search.HISTORY_TRACKS_SHARED_PREF
import com.example.yp_playlist.presentation.search.SearchPresenter
import com.example.yp_playlist.presentation.search.SearchView

object Creator {

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





