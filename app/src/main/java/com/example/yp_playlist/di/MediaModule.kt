package com.example.yp_playlist.di

import android.media.MediaPlayer
import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.data.date.DateManagerImpl
import com.example.yp_playlist.presentation.media.MediaViewModel
import com.example.yp_playlist.data.player.PlayerImpl
import com.example.yp_playlist.domain.interactors.media.MediaInteractor
import com.example.yp_playlist.domain.interactors.media.MediaInteractorImpl
import com.example.yp_playlist.data.player.Player
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mediaModule = module {

    viewModel {
        MediaViewModel(
            favouriteTracksInteractor = get(),
            mediaInteractor = get(),
            playlistInteractor = get()

        )
    }

    single<DateManager> {
        DateManagerImpl()
    }

    single<Player> {
        PlayerImpl(get())
    }

    single<MediaInteractor> {
        MediaInteractorImpl(
            mediaPlayer = get(),
            dateManager = get(),
            searchHistory = get(),
        )
    }

    single {
        MediaPlayer()
    }
}