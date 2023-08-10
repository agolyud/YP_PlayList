package com.example.yp_playlist.di

import com.example.yp_playlist.App
import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.data.date.DateManagerImpl
import com.example.yp_playlist.data.date.SearchHistory
import com.example.yp_playlist.data.date.SearchHistoryImpl
import com.example.yp_playlist.presentation.media.MediaViewModel
import com.example.yp_playlist.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mediaModule = module {

    viewModel {
        MediaViewModel(
            dateManager = get(),
            searchHistory = get()
        )
    }

    single<DateManager> {
        DateManagerImpl()
    }

    single<SearchHistory> {
        SearchHistoryImpl(get())
    }


}