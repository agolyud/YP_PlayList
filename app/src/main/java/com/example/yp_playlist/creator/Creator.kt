package com.example.yp_playlist.creator

import android.content.SharedPreferences
import com.example.yp_playlist.data.network.NetworkClientImpl
import com.example.yp_playlist.data.storage.SearchHistoryImpl
import com.example.yp_playlist.domain.interactor.TracksInteractorImpl
import com.example.yp_playlist.domain.repository.TracksRepositoryImpl
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
}





