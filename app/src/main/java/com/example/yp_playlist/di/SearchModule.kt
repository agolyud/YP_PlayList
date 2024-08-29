package com.example.yp_playlist.di

import android.content.Context
import com.example.yp_playlist.App
import com.example.yp_playlist.data.history.SearchHistory
import com.example.yp_playlist.data.history.SearchHistoryImpl
import com.example.yp_playlist.data.api.ITunesApi
import com.example.yp_playlist.data.network.NetworkClient
import com.example.yp_playlist.data.network.NetworkClientImpl
import com.example.yp_playlist.domain.interactors.tracks.TracksInteractor
import com.example.yp_playlist.domain.interactors.tracks.TracksInteractorImpl
import com.example.yp_playlist.domain.repository.TracksRepository
import com.example.yp_playlist.domain.repository.TracksRepositoryImpl
import com.example.yp_playlist.presentation.search.SearchViewModel
import com.example.yp_playlist.util.Constants.ITUNES_BASE_URL
import com.example.yp_playlist.util.Constants.PLAYLIST_SHARED_PREFERENCES
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val searchModule = module {

    viewModel {
        SearchViewModel(
            tracksInteractor = get(), androidApplication() as App
        )
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), searchHistory = get())
    }

    single<NetworkClient> {
        NetworkClientImpl(get(), androidContext())
    }

    factory<SearchHistory> {
        SearchHistoryImpl(get(), get())
    }

    factory {
        androidContext().getSharedPreferences(PLAYLIST_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    single {
        GsonBuilder().create()
    }

    single {
        Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get())).build()
    }

    single {
        get<Retrofit>().create(ITunesApi::class.java)
    }
}



