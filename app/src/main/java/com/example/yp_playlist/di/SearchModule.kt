package com.example.yp_playlist.di

import android.content.Context
import android.content.SharedPreferences
import com.example.yp_playlist.App
import com.example.yp_playlist.data.date.SearchHistory
import com.example.yp_playlist.data.date.SearchHistoryImpl
import com.example.yp_playlist.data.network.NetworkClient
import com.example.yp_playlist.data.network.NetworkClientImpl
import com.example.yp_playlist.domain.interactor.TracksInteractor
import com.example.yp_playlist.domain.interactor.TracksInteractorImpl
import com.example.yp_playlist.domain.repository.TracksRepository
import com.example.yp_playlist.domain.repository.TracksRepositoryImpl
import com.example.yp_playlist.presentation.search.HISTORY_TRACKS_SHARED_PREF
import com.example.yp_playlist.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val searchModule = module {

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            androidApplication() as App
        )
    }


    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), searchHistory = get())
    }


    single<NetworkClient> {
        NetworkClientImpl()
    }


    single<SearchHistory> {
        SearchHistoryImpl(
            provideSharedPreferences(
                context = get()
            )
        )
    }

}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(HISTORY_TRACKS_SHARED_PREF, Context.MODE_PRIVATE)
}

