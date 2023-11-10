package com.example.yp_playlist.di


import com.example.yp_playlist.db.converter.TrackDbConverter
import com.example.yp_playlist.medialibrary.favourite.data.FavouriteTracksRepositoryImpl
import com.example.yp_playlist.medialibrary.playlists.data.LocalStorageImpl
import com.example.yp_playlist.medialibrary.playlists.data.PlaylistRepositoryImpl
import com.example.yp_playlist.medialibrary.favourite.domain.api.FavouriteTracksInteractor
import com.example.yp_playlist.medialibrary.favourite.domain.api.FavouriteTracksRepository
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistRepository
import com.example.yp_playlist.medialibrary.favourite.domain.impl.FavouriteTracksInteractorImpl
import com.example.yp_playlist.medialibrary.playlists.domain.impl.PlaylistInteractorImpl
import com.example.yp_playlist.medialibrary.playlists.domain.models.LocalStorage
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.AddPlaylistViewModel
import com.example.yp_playlist.medialibrary.favourite.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel {
        FavouriteTracksViewModel(favouriteTracksInteractor = get())
    }

    viewModel {
        AddPlaylistViewModel(playlistInteractor = get())
    }

    viewModel {
        PlaylistsViewModel(playlistInteractor = get())
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
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(appDatabase = get(), trackDbConverter = get(),  localStorage = get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }

    single<LocalStorage> {
        LocalStorageImpl(context = get())
    }

}