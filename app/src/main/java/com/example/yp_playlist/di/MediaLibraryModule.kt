package com.example.yp_playlist.di


import com.example.yp_playlist.db.converter.TrackDbConverter
import com.example.yp_playlist.medialibrary.data.FavouriteTracksRepositoryImpl
import com.example.yp_playlist.medialibrary.domain.api.FavouriteTracksInteractor
import com.example.yp_playlist.medialibrary.domain.api.FavouriteTracksRepository
import com.example.yp_playlist.medialibrary.domain.impl.FavouriteTracksInteractorImpl
import com.example.yp_playlist.medialibrary.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.medialibrary.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel {
        FavouriteTracksViewModel(favouriteTracksInteractor = get())
    }

    viewModel {
        PlaylistsViewModel()
    }

    factory {
        TrackDbConverter()
    }

    single<FavouriteTracksRepository> {
        FavouriteTracksRepositoryImpl(appDatabase = get(), trackDbConvertor = get())
    }

    single<FavouriteTracksInteractor> {
        FavouriteTracksInteractorImpl(favouriteTracksRepository = get())
    }
}